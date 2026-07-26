package com.rafalskrzypczyk.auth.data

import android.content.Context
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.GoogleCredentialsProvider
import com.rafalskrzypczyk.auth.domain.toDTO
import com.rafalskrzypczyk.auth.domain.toDomain
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.domain.ScoreManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val ORIGIN_LOGIN_WITH_EMAIL = "AuthRepository.loginWithEmailAndPassword"
private const val ORIGIN_REGISTER_WITH_EMAIL = "AuthRepository.registerWithEmailAndPassword"
private const val ORIGIN_SEND_PASSWORD_RESET = "AuthRepository.sendPasswordResetToEmail"
private const val ORIGIN_REAUTHENTICATE_WITH_PASSWORD = "AuthRepository.reauthenticateWithPassword"
private const val ORIGIN_REAUTHENTICATE_WITH_PROVIDER = "AuthRepository.reauthenticateWithProvider"
private const val ORIGIN_CHANGE_PASSWORD = "AuthRepository.changePassword"
private const val ORIGIN_CHANGE_USER_NAME = "AuthRepository.changeUserName"
private const val ORIGIN_DELETE_USER = "AuthRepository.deleteUser"
private const val ORIGIN_SIGN_IN_WITH_GOOGLE = "AuthRepository.signInWithGoogle"
private const val ORIGIN_REGISTER_USER = "AuthRepository.registerUser"
private const val ORIGIN_LOGIN_USER = "AuthRepository.loginUser"

private const val PROVIDER_ID_PASSWORD = "password"

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreApi: FirestoreApi,
    private val userManager: UserManager,
    private val authErrorMapper: AuthErrorMapper,
    private val googleCredentialsProvider: GoogleCredentialsProvider,
    private val scoreManager: ScoreManager
) : AuthRepository {
    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ): Flow<Response<UserData>> = flow {
        emit(Response.Loading)

        val result = try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_LOGIN_WITH_EMAIL, e)))
            return@flow
        }

        val user = result?.user
        if (user == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_LOGIN_WITH_EMAIL, AppError.Auth.SignInIncomplete)))
            return@flow
        }

        loginUser(user).collect { emit(it) }
    }

    override fun registerWithEmailAndPassword(
        email: String,
        password: String,
        userName: String,
    ): Flow<Response<UserData>> = flow {
        emit(Response.Loading)

        val result = try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_REGISTER_WITH_EMAIL, e)))
            return@flow
        }

        val user = result?.user
        if (user == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_REGISTER_WITH_EMAIL, AppError.Auth.SignInIncomplete)))
            return@flow
        }

        user.sendEmailVerification()

        registerUser(user, email, userName).collect { emit(it) }
    }

    override fun signOut() {
        firebaseAuth.signOut()
        userManager.clearUserDataLocal()
        scoreManager.onUserLogOut()
    }

    override fun sendPasswordResetToEmail(email: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Response.Success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_SEND_PASSWORD_RESET, e)))
        }
    }

    override fun reauthenticateWithPassword(
        email: String,
        password: String,
    ): Flow<Response<Unit>> = flow {
        emit(Response.Loading)

        val user = firebaseAuth.currentUser
        if (user == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_REAUTHENTICATE_WITH_PASSWORD, AppError.Auth.UserNotLoggedIn)))
            return@flow
        }

        try {
            user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
            emit(Response.Success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_REAUTHENTICATE_WITH_PASSWORD, e)))
        }
    }

    override fun reauthenticateWithProvider(context: Context): Flow<Response<Unit>> = flow {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_REAUTHENTICATE_WITH_PROVIDER, AppError.Auth.UserNotLoggedIn)))
            return@flow
        }

        val idToken = try {
            googleCredentialsProvider.getGoogleIdToken(context)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val error = authErrorMapper.toAppError(ORIGIN_REAUTHENTICATE_WITH_PROVIDER, e)
            if (error != AppError.Google.Cancelled) emit(Response.Error(error))
            return@flow
        }

        emit(Response.Loading)

        if (idToken == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_REAUTHENTICATE_WITH_PROVIDER, AppError.Google.UnsupportedCredential)))
            return@flow
        }

        try {
            currentUser.reauthenticate(GoogleAuthProvider.getCredential(idToken, null)).await()
            emit(Response.Success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_REAUTHENTICATE_WITH_PROVIDER, e)))
        }
    }

    override fun changePassword(newPassword: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading)

        val user = firebaseAuth.currentUser
        if (user == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_CHANGE_PASSWORD, AppError.Auth.UserNotLoggedIn)))
            return@flow
        }

        try {
            user.updatePassword(newPassword).await()
            emit(Response.Success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_CHANGE_PASSWORD, e)))
        }
    }

    override fun changeUserName(newUsername: String): Flow<Response<UserData>> = flow {
        emit(Response.Loading)

        val currentUserData = userManager.getCurrentLoggedUser()
        if (currentUserData == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_CHANGE_USER_NAME, AppError.Auth.UserNotLoggedIn)))
            return@flow
        }

        val updatedUserData = currentUserData.copy(name = newUsername)
        firestoreApi.updateUserData(updatedUserData.toDTO()).collect { response ->
            when (response) {
                is Response.Error -> emit(response)
                Response.Loading -> emit(Response.Loading)
                is Response.Success -> {
                    userManager.saveUserDataInLocal(updatedUserData)
                    emit(Response.Success(updatedUserData))
                }
            }
        }
    }

    override fun deleteUser(): Flow<Response<Unit>> = channelFlow {
        send(Response.Loading)

        firebaseAuth.currentUser?.delete()?.addOnFailureListener {
            trySend(Response.Error(authErrorMapper.toAppError(ORIGIN_DELETE_USER, it)))
        }

        val userId = userManager.getCurrentLoggedUser()?.id
        if (userId == null) {
            send(Response.Error(authErrorMapper.report(ORIGIN_DELETE_USER, AppError.Auth.UserNotLoggedIn)))
            return@channelFlow
        }

        firestoreApi.deleteUserData(userId).collect {
            when (it) {
                is Response.Error -> send(it)
                Response.Loading -> send(Response.Loading)
                is Response.Success -> {
                    userManager.clearUserDataLocal()
                    scoreManager.onUserDelete()
                    send(Response.Success(Unit))
                }
            }
        }
    }

    override fun signInWithGoogle(context: Context): Flow<Response<UserData>> = flow {
        val idToken = try {
            googleCredentialsProvider.getGoogleIdToken(context)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val error = authErrorMapper.toAppError(ORIGIN_SIGN_IN_WITH_GOOGLE, e)
            if (error != AppError.Google.Cancelled) emit(Response.Error(error))
            return@flow
        }

        emit(Response.Loading)

        if (idToken == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_SIGN_IN_WITH_GOOGLE, AppError.Google.UnsupportedCredential)))
            return@flow
        }

        val authResult = try {
            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_SIGN_IN_WITH_GOOGLE, e)))
            return@flow
        }

        val user = authResult?.user
        if (user == null) {
            emit(Response.Error(authErrorMapper.report(ORIGIN_SIGN_IN_WITH_GOOGLE, AppError.Auth.SignInIncomplete)))
            return@flow
        }

        if (authResult.additionalUserInfo?.isNewUser ?: true) {
            registerUser(user, user.email.orEmpty(), user.displayName.orEmpty()).collect { emit(it) }
        } else {
            loginUser(user).collect { emit(it) }
        }
    }

    private fun registerUser(user: FirebaseUser, email: String, userName: String): Flow<Response<UserData>> = flow {
        val authMethod = try {
            user.providerData[1].providerId
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_REGISTER_USER, e)))
            return@flow
        }

        val newUser = UserData(
            user.uid,
            email,
            userName,
            authenticationMethod = authenticationMethodOf(authMethod)
        )

        firestoreApi.updateUserData(newUser.toDTO()).collect {
            when (it) {
                is Response.Loading -> emit(it)
                is Response.Error -> emit(it)
                is Response.Success -> {
                    userManager.saveUserDataInLocal(newUser)
                    scoreManager.onUserRegister()
                    emit(Response.Success(newUser))
                }
            }
        }
    }

    private fun loginUser(user: FirebaseUser): Flow<Response<UserData>> = flow {
        val authMethod = try {
            user.providerData[1].providerId
        } catch (e: Exception) {
            emit(Response.Error(authErrorMapper.toAppError(ORIGIN_LOGIN_USER, e)))
            return@flow
        }

        firestoreApi.getUserData(user.uid).collect {
            when (it) {
                is Response.Error -> emit(it)
                is Response.Loading -> emit(it)
                is Response.Success -> {
                    val userData = it.data.toDomain(
                        email = user.email.orEmpty(),
                        authMethod = authenticationMethodOf(authMethod)
                    )
                    userManager.saveUserDataInLocal(userData)
                    scoreManager.onUserLogIn()
                    emit(Response.Success(userData))
                }
            }
        }
    }

    private fun authenticationMethodOf(providerId: String): UserAuthenticationMethod =
        if (providerId == PROVIDER_ID_PASSWORD) UserAuthenticationMethod.PASSWORD
        else UserAuthenticationMethod.NONPASSWORD
}

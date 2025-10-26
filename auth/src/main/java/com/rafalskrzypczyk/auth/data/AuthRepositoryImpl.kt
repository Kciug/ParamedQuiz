package com.rafalskrzypczyk.auth.data

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.rafalskrzypczyk.auth.R
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.toDTO
import com.rafalskrzypczyk.auth.domain.toDomain
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.FirebaseError
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.domain.ScoreManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreApi: FirestoreApi,
    private val userManager: UserManager,
    private val firebaseError: FirebaseError,
    private val scoreManager: ScoreManager
) : AuthRepository {
    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ): Flow<Response<UserData>> = channelFlow {
        try {
            send(Response.Loading)

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            loginUser(user = result.user!!).collectLatest {
                send(it)
            }
        } catch (e: Exception) {
            send(Response.Error(firebaseError.localizedError((e as? FirebaseAuthException)?.errorCode ?: "")))
        }
    }

    override fun registerWithEmailAndPassword(
        email: String,
        password: String,
        userName: String,
    ): Flow<Response<UserData>> = channelFlow {
        try {
            send(Response.Loading)

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user!!.sendEmailVerification()

            registerUser(result.user!!, email, userName).collectLatest {
                send(it)
            }
        } catch (e: Exception) {
            send(Response.Error(firebaseError.localizedError((e as? FirebaseAuthException)?.errorCode ?: "")))
        }
    }

    override fun signOut() {
//        val currentUser = firebaseAuth.currentUser

        firebaseAuth.signOut()
        userManager.clearUserDataLocal()
        scoreManager.onUserLogOut()

//        if(currentUser?.providerId == "google.com") {
//            clearGoogleCredentialState(context)
//        }
    }

    override fun sendPasswordResetToEmail(email: String): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnFailureListener {
                trySend(Response.Error(firebaseError.localizedError((it as? FirebaseAuthException)?.errorCode ?: "")))
            }.addOnSuccessListener {
                trySend(Response.Success(Unit))
            }
        awaitClose { this.cancel() }
    }

    override fun reauthenticateWithPassword(
        email: String,
        password: String,
    ): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        val user = firebaseAuth.currentUser!!
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential).addOnSuccessListener {
            trySend(Response.Success(Unit))
        }.addOnFailureListener {
            trySend(Response.Error(firebaseError.localizedError((it as? FirebaseAuthException)?.errorCode ?: "")))
        }
        awaitClose { this.cancel() }
    }

    override fun reauthenticateWithProvider(context: Context): Flow<Response<Unit>> = callbackFlow {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            send(Response.Error("Weryfikacja nieudana. Wyloguj się i spróbuj po ponownym zalogowaniu"))
            return@callbackFlow
        }

        try {
            val result = getGoogleCredentials(context)

            send(Response.Loading)

            val validAuthCredentials = validateGoogleCredentials(result.credential)

            if (validAuthCredentials != null) {
                currentUser.reauthenticate(validAuthCredentials).addOnSuccessListener {
                    trySend(Response.Success(Unit))
                }.addOnFailureListener {
                    trySend(Response.Error(firebaseError.localizedError((it as? FirebaseAuthException)?.errorCode ?: "")))
                }
            } else {
                send(Response.Error("Coś poszło nie tak. Spróbuj później"))
            }
        } catch (e: Exception) {
            Log.d("SignInWithGoogle", e.toString())
            if(e !is GetCredentialCancellationException)
                send(Response.Error(firebaseError.localizedError((e as? FirebaseAuthException)?.errorCode ?: "")))
        }

        awaitClose { this.cancel() }
    }

    override fun changePassword(newPassword: String): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        firebaseAuth.currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
            trySend(Response.Success(Unit))
        }?.addOnFailureListener {
            trySend(Response.Error(firebaseError.localizedError((it as? FirebaseAuthException)?.errorCode ?: "")))
        }
        awaitClose { this.cancel() }
    }

    override fun changeUserName(newUsername: String): Flow<Response<UserData>> = callbackFlow {
        trySend(Response.Loading)
        userManager.getCurrentLoggedUser()?.let {
            val updatedUserData = it.copy(name = newUsername)
            firestoreApi.updateUserData(updatedUserData.toDTO()).collectLatest { response ->
                when (response) {
                    is Response.Error -> trySend(Response.Error(response.error))
                    Response.Loading -> trySend(Response.Loading)
                    is Response.Success -> {
                        userManager.saveUserDataInLocal(updatedUserData)
                        trySend(Response.Success(updatedUserData))
                    }
                }
            }
        }
        awaitClose { this.cancel() }
    }

    override fun deleteUser(): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        val currentUser = firebaseAuth.currentUser

        currentUser?.delete()?.addOnFailureListener {
            trySend(Response.Error(firebaseError.localizedError((it as? FirebaseAuthException)?.errorCode ?: "")))
        }

        firestoreApi.deleteUserData(userManager.getCurrentLoggedUser()!!.id).collectLatest {
            when (it) {
                is Response.Error -> trySend(Response.Error(it.error))
                Response.Loading -> trySend(Response.Loading)
                is Response.Success -> {
                    userManager.clearUserDataLocal()
                    scoreManager.onUserDelete()
                    trySend(Response.Success(Unit))
                }
            }
        }

//        if(currentUser?.providerId == "google.com") {
//            clearGoogleCredentialState(context)
//        }

        awaitClose { this.cancel() }
    }

    override fun signInWithGoogle(context: Context): Flow<Response<UserData>> = callbackFlow {
        try {
            val result = getGoogleCredentials(context)

            send(Response.Loading)

            val validGoogleCredentials = validateGoogleCredentials(result.credential)

            val authResult = if(validGoogleCredentials != null) {
                firebaseAuth.signInWithCredential(validGoogleCredentials).await()
            } else {
                null
            }

            with(authResult) {
                if (this == null) {
                    send(Response.Error("Coś poszło nie tak. Spróbuj później"))
                } else {
                    if (additionalUserInfo?.isNewUser ?: true) {
                        registerUser(
                            this.user!!,
                            this.user!!.email ?: "",
                            this.user!!.displayName ?: ""
                        ).collectLatest {
                            send(it)
                        }
                    } else {
                        loginUser(this.user!!).collectLatest {
                            send(it)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("SignInWithGoogle", e.toString())
            if(e !is GetCredentialCancellationException)
                send(Response.Error(firebaseError.localizedError((e as? FirebaseAuthException)?.errorCode ?: "")))
        }


        awaitClose { this.cancel() }
    }

    private fun registerUser(user: FirebaseUser, email: String, userName: String): Flow<Response<UserData>> = channelFlow {
        val authMethod = user.providerId

        val newUser = UserData(
            user.uid,
            email,
            userName,
            authenticationMethod = if(authMethod == "password") UserAuthenticationMethod.PASSWORD else UserAuthenticationMethod.NONPASSWORD
        )

        firestoreApi.updateUserData(newUser.toDTO()).collectLatest {
            when (it) {
                is Response.Loading -> send(it)
                is Response.Error -> send(it)
                is Response.Success -> {
                    userManager.saveUserDataInLocal(newUser)
                    scoreManager.onUserRegister()
                    send(Response.Success(newUser))
                }
            }
        }
    }

    private fun loginUser(user: FirebaseUser): Flow<Response<UserData>> = channelFlow {
        val authMethod = user.providerId

        firestoreApi.getUserData(user.uid).collectLatest {
            when (it) {
                is Response.Error -> send(it)
                is Response.Loading -> send(it)
                is Response.Success -> {
                    val userData = it.data.toDomain(
                        email = user.email ?: "",
                        authMethod = if (authMethod == "password") UserAuthenticationMethod.PASSWORD else UserAuthenticationMethod.NONPASSWORD
                    )
                    userManager.saveUserDataInLocal(userData)
                    scoreManager.onUserLogIn()
                    send(Response.Success(userData))
                }
            }
        }
    }

    private suspend fun getGoogleCredentials(context: Context): GetCredentialResponse {
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = context.getString(R.string.web_client_id)
        )
            .setNonce("")
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val credentialManager = CredentialManager.create(context)

        return credentialManager.getCredential(
            request = request,
            context = context,
        )
    }

    private fun validateGoogleCredentials(credential: Credential): AuthCredential? {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val validCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            return validCredential
        } else {
            return null
        }
    }

    private suspend fun clearGoogleCredentialState(context: Context) {
        val clearRequest = ClearCredentialStateRequest()
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(clearRequest)
    }
}
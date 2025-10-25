package com.rafalskrzypczyk.auth.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.rafalskrzypczyk.auth.R
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.toDTO
import com.rafalskrzypczyk.auth.domain.toDomain
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.FirebaseError
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val scoreManager: ScoreManager,
    @ApplicationContext private val context: Context
) : AuthRepository {
    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ): Flow<Response<UserData>> = channelFlow {
        try {
            send(Response.Loading)

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            firestoreApi.getUserData(result.user!!.uid).collectLatest {
                when (it) {
                    is Response.Error -> throw Exception(it.error)
                    Response.Loading -> send(Response.Loading)
                    is Response.Success -> {
                        val userData = it.data.toDomain(result.user!!.email ?: "")
                        userManager.saveUserDataInLocal(userData)
                        scoreManager.onUserLogIn()
                        send(Response.Success(userData))
                    }
                }
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

            val newUser = UserData(
                result.user!!.uid,
                email,
                userName
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
        } catch (e: Exception) {
            send(Response.Error(firebaseError.localizedError((e as? FirebaseAuthException)?.errorCode ?: "")))
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
        userManager.clearUserDataLocal()
        scoreManager.onUserLogOut()
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

    override fun reauthenticate(
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
            firestoreApi.updateUserData(updatedUserData.toDTO()).collectLatest {
                when (it) {
                    is Response.Error -> trySend(Response.Error(it.error))
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
        firebaseAuth.currentUser?.delete()?.addOnFailureListener {
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

        awaitClose { this.cancel() }
    }

    override fun signInWithGoogle(): Flow<Response<UserData>> = callbackFlow {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = context.getString(R.string.web_client_id)
            )
                .setNonce("")
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val credentialManager = CredentialManager.create(context)

            try {
                val result = credentialManager
                    .getCredential(
                        request = request,
                        context = context,
                    )

                send(Response.Loading)

                val authResult = handleSignIn(result.credential)

                with(authResult) {
                    if (this == null) {
                        send(Response.Error("Something went wrong"))
                    } else {
                        val user = UserData(
                            this.user!!.uid,
                            this.user!!.email ?: "",
                            this.user!!.displayName ?: ""
                        )

                        if(additionalUserInfo?.isNewUser ?: true) {
                            firestoreApi.updateUserData(user.toDTO()).collectLatest {
                                when (it) {
                                    is Response.Loading -> send(it)
                                    is Response.Error -> send(it)
                                    is Response.Success -> {
                                        userManager.saveUserDataInLocal(user)
                                        scoreManager.onUserRegister()
                                        send(Response.Success(user))
                                    }
                                }
                            }
                        } else {
                            scoreManager.onUserLogIn()
                            send(Response.Success(user))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("SignInWithGoogle", e.toString())
                if(e !is GetCredentialCancellationException)
                    send(Response.Error(firebaseError.localizedError((e as? FirebaseAuthException)?.errorCode ?: "")))
            }
        }
        awaitClose { this.cancel() }
    }

    private suspend fun handleSignIn(credential: Credential): AuthResult? {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            return firebaseAuthWithCredential(credential)
        } else {
            return null
        }
    }

    private suspend fun firebaseAuthWithCredential(authCredential: AuthCredential): AuthResult {
        return firebaseAuth.signInWithCredential(authCredential).await()
    }
}
package com.rafalskrzypczyk.auth.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.toDTO
import com.rafalskrzypczyk.auth.domain.toDomain
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.FirebaseError
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.ScoreManager
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
    ): Flow<Response<UserData>> = channelFlow<Response<UserData>> {
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
                    trySend(Response.Success(Unit))
                }
            }
        }

        awaitClose { this.cancel() }
    }
}
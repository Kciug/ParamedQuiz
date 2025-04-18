package com.rafalskrzypczyk.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.toDTO
import com.rafalskrzypczyk.auth.domain.toDomain
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
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
    private val resourcesProvider: ResourceProvider,
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
                        send(Response.Success(userData))
                    }
                }
            }
        } catch (e: Exception) {
            send(Response.Error(e.localizedMessage ?: resourcesProvider.getString(R.string.error_unknown)))
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
            send(Response.Error(e.localizedMessage ?: resourcesProvider.getString(R.string.error_unknown)))
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
        userManager.clearUserDataLocal()
    }

    override fun sendPasswordResetToEmail(email: String): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnFailureListener {
                trySend(Response.Error(it.localizedMessage ?: resourcesProvider.getString(R.string.error_unknown)))
            }.addOnSuccessListener {
                trySend(Response.Success(Unit))
            }
        awaitClose { this.cancel() }
    }

    override fun changePassword(newPassword: String): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        firebaseAuth.currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
            trySend(Response.Success(Unit))
        }?.addOnFailureListener {
            trySend(Response.Error(it.localizedMessage ?: resourcesProvider.getString(R.string.error_unknown)))
        }
        awaitClose { this.cancel() }
    }

    override fun deleteUser(): Flow<Response<Unit>> = callbackFlow {
        trySend(Response.Loading)
        firebaseAuth.currentUser?.delete()?.addOnSuccessListener {
            userManager.clearUserDataLocal()
            trySend(Response.Success(Unit))
        }?.addOnFailureListener {
            trySend(Response.Error(it.localizedMessage ?: resourcesProvider.getString(R.string.error_unknown)))
        }
        awaitClose { this.cancel() }
    }
}
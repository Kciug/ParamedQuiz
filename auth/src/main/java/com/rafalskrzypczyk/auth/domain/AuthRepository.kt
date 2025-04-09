package com.rafalskrzypczyk.auth.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isUserLoggedIn() : Boolean
    fun loginWithEmailAndPassword(email: String, password: String) : Flow<Response<UserData>>
    fun registerWithEmailAndPassword(email: String, password: String, userName: String) : Flow<Response<UserData>>
    fun signOut()
    fun sendPasswordResetToEmail(email: String) : Flow<Response<Unit>>
    fun changePassword(newPassword: String) : Flow<Response<Unit>>
    fun deleteUser() : Flow<Response<Unit>>
}


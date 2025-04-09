package com.rafalskrzypczyk.core.user_management

interface UserManager {
    fun getCurrentLoggedUser(): UserData?
    fun saveUserDataInLocal(user: UserData)
    fun clearUserDataLocal()
}
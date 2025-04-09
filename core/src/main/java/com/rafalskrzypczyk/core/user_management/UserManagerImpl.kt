package com.rafalskrzypczyk.core.user_management

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) : UserManager {
    override fun getCurrentLoggedUser(): UserData? = sharedPreferences.getCurrentUser()

    override fun saveUserDataInLocal(user: UserData) {
        sharedPreferences.setCurrentUser(user)
    }

    override fun clearUserDataLocal() {
        sharedPreferences.clearUserData()
    }
}
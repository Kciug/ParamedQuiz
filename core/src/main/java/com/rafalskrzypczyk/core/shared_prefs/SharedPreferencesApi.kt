package com.rafalskrzypczyk.core.shared_prefs

import com.rafalskrzypczyk.core.user_management.UserData

interface SharedPreferencesApi {
    fun setCurrentUser(userData: UserData)
    fun getCurrentUser(): UserData?
    fun clearUserData()

    fun setOnboardingStatus(done: Boolean)
    fun getOnboardingStatus(): Boolean
}
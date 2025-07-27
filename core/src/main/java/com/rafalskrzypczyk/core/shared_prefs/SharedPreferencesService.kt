package com.rafalskrzypczyk.core.shared_prefs

import android.content.SharedPreferences
import com.rafalskrzypczyk.core.user_management.UserData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SharedPreferencesService @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SharedPreferencesApi {
    companion object {
        const val KEY_CURRENT_USER = "current_user"

        const val DEFAULT_STRING_VALUE = ""
    }

    override fun setCurrentUser(userData: UserData) {
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, Json.encodeToString(userData))
            .apply()
    }

    override fun getCurrentUser(): UserData? {
        val json = sharedPreferences.getString(KEY_CURRENT_USER, null)
        if (json.isNullOrEmpty()) return null
        return Json.decodeFromString<UserData>(json)
    }

    override fun clearUserData() {
        sharedPreferences.edit()
            .remove(KEY_CURRENT_USER)
            .apply()
    }
}
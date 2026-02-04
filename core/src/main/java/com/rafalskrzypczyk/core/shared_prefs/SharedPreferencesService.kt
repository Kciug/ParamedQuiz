package com.rafalskrzypczyk.core.shared_prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.rafalskrzypczyk.core.user_management.UserData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SharedPreferencesService @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SharedPreferencesApi {
    companion object {
        const val KEY_CURRENT_USER = "current_user"
        const val KEY_ONBOARDING_STATUS = "onboarding_done"
        const val KEY_ONBOARDING_MAIN_MODE_STATUS = "onboarding_main_mode_done"
        const val KEY_ACCEPTED_TERMS_VERSION = "accepted_terms_version"

        const val DEFAULT_STRING_VALUE = ""
    }

    override fun setCurrentUser(userData: UserData) {
        sharedPreferences.edit {
            putString(KEY_CURRENT_USER, Json.encodeToString(userData))
        }
    }

    override fun getCurrentUser(): UserData? {
        val json = sharedPreferences.getString(KEY_CURRENT_USER, null)
        if (json.isNullOrEmpty()) return null
        return Json.decodeFromString<UserData>(json)
    }

    override fun clearUserData() {
        sharedPreferences.edit {
            remove(KEY_CURRENT_USER)
        }
    }

    override fun setOnboardingStatus(done: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_STATUS, done)
        }
    }

    override fun getOnboardingStatus(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_STATUS, false)
    }

    override fun setMainModeOnboardingSeen(seen: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_MAIN_MODE_STATUS, seen)
        }
    }

    override fun getMainModeOnboardingSeen(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_MAIN_MODE_STATUS, false)
    }

    override fun getAcceptedTermsVersion(): Int {
        return sharedPreferences.getInt(KEY_ACCEPTED_TERMS_VERSION, -1)
    }

    override fun setAcceptedTermsVersion(version: Int) {
        sharedPreferences.edit {
            putInt(KEY_ACCEPTED_TERMS_VERSION, version)
        }
    }
}
package com.rafalskrzypczyk.core.shared_prefs

import com.rafalskrzypczyk.core.user_management.UserData

interface SharedPreferencesApi {
    fun setCurrentUser(userData: UserData)
    fun getCurrentUser(): UserData?
    fun clearUserData()

    fun setOnboardingStatus(done: Boolean)
    fun getOnboardingStatus(): Boolean

    fun setMainModeOnboardingSeen(seen: Boolean)
    fun getMainModeOnboardingSeen(): Boolean

    fun setSwipeModeOnboardingSeen(seen: Boolean)
    fun getSwipeModeOnboardingSeen(): Boolean

    fun setTranslationModeOnboardingSeen(seen: Boolean)
    fun getTranslationModeOnboardingSeen(): Boolean

    fun resetModularOnboarding()

    fun getAcceptedTermsVersion(): Int
    fun setAcceptedTermsVersion(version: Int)

    fun getInstallDate(): Long
    fun setInstallDate(timestamp: Long)

    fun getCompletedQuizzesCount(): Int
    fun incrementCompletedQuizzesCount()
    fun resetCompletedQuizzesCount()

    fun isAppRated(): Boolean
    fun setAppRated(rated: Boolean)

    fun isRatingPromptDisabled(): Boolean
    fun setRatingPromptDisabled(disabled: Boolean)

    fun getLastRatingPromptDate(): Long
    fun setLastRatingPromptDate(timestamp: Long)
}

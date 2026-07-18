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
        const val KEY_ONBOARDING_SWIPE_MODE_STATUS = "onboarding_swipe_mode_done"
        const val KEY_ONBOARDING_TRANSLATION_MODE_STATUS = "onboarding_translation_mode_done"
        const val KEY_ONBOARDING_CEM_MODE_STATUS = "onboarding_cem_mode_done"
        const val KEY_ACCEPTED_TERMS_VERSION = "accepted_terms_version"
        const val KEY_INSTALL_DATE = "install_date"
        const val KEY_COMPLETED_QUIZZES_COUNT = "completed_quizzes_count"
        const val KEY_APP_RATED = "app_rated"
        const val KEY_RATING_PROMPT_DISABLED = "rating_prompt_disabled"
        const val KEY_LAST_RATING_PROMPT_DATE = "last_rating_prompt_date"
        const val KEY_SEEN_NEWS_IDS = "seen_news_ids"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_REMINDERS_ENABLED = "reminders_enabled"
        const val KEY_NEWS_ENABLED = "news_enabled"
        const val KEY_MARKETING_ENABLED = "marketing_enabled"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        const val KEY_REMINDER_HOUR = "reminder_hour"
        const val KEY_REMINDER_MINUTE = "reminder_minute"
        const val KEY_NOTIFICATION_PROMPT_COUNT = "notification_prompt_count"
        const val KEY_LAST_NOTIFICATION_PROMPT_DATE = "last_notification_prompt_date"
        const val KEY_NOTIFICATION_PROMPT_DISABLED = "notification_prompt_disabled"
        const val KEY_LAST_WINBACK_DAY_SENT = "last_winback_day_sent"
        const val KEY_LAST_REVISION_REMINDER_DATE = "last_revision_reminder_date"

        const val DEFAULT_STRING_VALUE = ""
        const val DEFAULT_REMINDER_HOUR = 19
        const val DEFAULT_REMINDER_MINUTE = 0
    }

    override fun getSeenNewsIds(): Set<String> {
        return sharedPreferences.getStringSet(KEY_SEEN_NEWS_IDS, emptySet()) ?: emptySet()
    }

    override fun addSeenNewsId(id: String) {
        val current = getSeenNewsIds().toMutableSet()
        current.add(id)
        sharedPreferences.edit {
            putStringSet(KEY_SEEN_NEWS_IDS, current)
        }
    }

    override fun clearSeenNewsIds() {
        sharedPreferences.edit {
            remove(KEY_SEEN_NEWS_IDS)
        }
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

    override fun setSwipeModeOnboardingSeen(seen: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_SWIPE_MODE_STATUS, seen)
        }
    }

    override fun getSwipeModeOnboardingSeen(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_SWIPE_MODE_STATUS, false)
    }

    override fun setTranslationModeOnboardingSeen(seen: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_TRANSLATION_MODE_STATUS, seen)
        }
    }

    override fun getTranslationModeOnboardingSeen(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_TRANSLATION_MODE_STATUS, false)
    }

    override fun setCemModeOnboardingSeen(seen: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_CEM_MODE_STATUS, seen)
        }
    }

    override fun getCemModeOnboardingSeen(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_CEM_MODE_STATUS, false)
    }

    override fun resetModularOnboarding() {
        sharedPreferences.edit {
            remove(KEY_ONBOARDING_MAIN_MODE_STATUS)
            remove(KEY_ONBOARDING_SWIPE_MODE_STATUS)
            remove(KEY_ONBOARDING_TRANSLATION_MODE_STATUS)
            remove(KEY_ONBOARDING_CEM_MODE_STATUS)
        }
    }

    override fun getAcceptedTermsVersion(): Int {
        return sharedPreferences.getInt(KEY_ACCEPTED_TERMS_VERSION, -1)
    }

    override fun setAcceptedTermsVersion(version: Int) {
        sharedPreferences.edit {
            putInt(KEY_ACCEPTED_TERMS_VERSION, version)
        }
    }

    override fun getInstallDate(): Long {
        return sharedPreferences.getLong(KEY_INSTALL_DATE, 0L)
    }

    override fun setInstallDate(timestamp: Long) {
        sharedPreferences.edit {
            putLong(KEY_INSTALL_DATE, timestamp)
        }
    }

    override fun getCompletedQuizzesCount(): Int {
        return sharedPreferences.getInt(KEY_COMPLETED_QUIZZES_COUNT, 0)
    }

    override fun incrementCompletedQuizzesCount() {
        val currentCount = getCompletedQuizzesCount()
        sharedPreferences.edit {
            putInt(KEY_COMPLETED_QUIZZES_COUNT, currentCount + 1)
        }
    }

    override fun resetCompletedQuizzesCount() {
        sharedPreferences.edit {
            putInt(KEY_COMPLETED_QUIZZES_COUNT, 0)
        }
    }

    override fun isAppRated(): Boolean {
        return sharedPreferences.getBoolean(KEY_APP_RATED, false)
    }

    override fun setAppRated(rated: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_APP_RATED, rated)
        }
    }

    override fun isRatingPromptDisabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_RATING_PROMPT_DISABLED, false)
    }

    override fun setRatingPromptDisabled(disabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_RATING_PROMPT_DISABLED, disabled)
        }
    }

    override fun getLastRatingPromptDate(): Long {
        return sharedPreferences.getLong(KEY_LAST_RATING_PROMPT_DATE, 0L)
    }

    override fun setLastRatingPromptDate(timestamp: Long) {
        sharedPreferences.edit {
            putLong(KEY_LAST_RATING_PROMPT_DATE, timestamp)
        }
    }

    override fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    override fun isRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMINDERS_ENABLED, true)
    }

    override fun setRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_REMINDERS_ENABLED, enabled)
        }
    }

    override fun isNewsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NEWS_ENABLED, true)
    }

    override fun setNewsEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_NEWS_ENABLED, enabled)
        }
    }

    override fun isMarketingEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_MARKETING_ENABLED, true)
    }

    override fun setMarketingEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_MARKETING_ENABLED, enabled)
        }
    }

    override fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
    }

    override fun setSoundEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_SOUND_ENABLED, enabled)
        }
    }

    override fun isHapticEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAPTIC_ENABLED, true)
    }

    override fun setHapticEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_HAPTIC_ENABLED, enabled)
        }
    }

    override fun getReminderHour(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
    }

    override fun getReminderMinute(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)
    }

    override fun setReminderTime(hour: Int, minute: Int) {
        sharedPreferences.edit {
            putInt(KEY_REMINDER_HOUR, hour)
            putInt(KEY_REMINDER_MINUTE, minute)
        }
    }

    override fun getNotificationPromptCount(): Int {
        return sharedPreferences.getInt(KEY_NOTIFICATION_PROMPT_COUNT, 0)
    }

    override fun incrementNotificationPromptCount() {
        val current = getNotificationPromptCount()
        sharedPreferences.edit {
            putInt(KEY_NOTIFICATION_PROMPT_COUNT, current + 1)
        }
    }

    override fun resetNotificationPromptCount() {
        sharedPreferences.edit {
            putInt(KEY_NOTIFICATION_PROMPT_COUNT, 0)
        }
    }

    override fun getLastNotificationPromptDate(): Long {
        return sharedPreferences.getLong(KEY_LAST_NOTIFICATION_PROMPT_DATE, 0L)
    }

    override fun setLastNotificationPromptDate(timestamp: Long) {
        sharedPreferences.edit {
            putLong(KEY_LAST_NOTIFICATION_PROMPT_DATE, timestamp)
        }
    }

    override fun isNotificationPromptDisabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_PROMPT_DISABLED, false)
    }

    override fun setNotificationPromptDisabled(disabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_NOTIFICATION_PROMPT_DISABLED, disabled)
        }
    }

    override fun getLastWinbackDaySent(): Int {
        return sharedPreferences.getInt(KEY_LAST_WINBACK_DAY_SENT, 0)
    }

    override fun setLastWinbackDaySent(day: Int) {
        sharedPreferences.edit {
            putInt(KEY_LAST_WINBACK_DAY_SENT, day)
        }
    }

    override fun getLastRevisionReminderDate(): Long {
        return sharedPreferences.getLong(KEY_LAST_REVISION_REMINDER_DATE, 0L)
    }

    override fun setLastRevisionReminderDate(timestamp: Long) {
        sharedPreferences.edit {
            putLong(KEY_LAST_REVISION_REMINDER_DATE, timestamp)
        }
    }
}

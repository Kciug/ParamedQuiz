package com.rafalskrzypczyk.core.domain.use_cases

import com.rafalskrzypczyk.core.domain.config.NotificationConsentConfig
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Decyduje, czy pokazać pre-permission prompt powiadomień (priming).
 * Analogiczne do [CheckAppRatingEligibilityUC] — czyta wyłącznie preferencje (bez Contextu).
 */
class CheckNotificationConsentEligibilityUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke(): Boolean {
        if (sharedPrefs.isNotificationsEnabled()) return false
        if (sharedPrefs.isNotificationPromptDisabled()) return false

        // Pierwsza ukończona sesja musiała się wydarzyć.
        if (sharedPrefs.getCompletedQuizzesCount() < NotificationConsentConfig.MIN_COMPLETED_QUIZZES) return false

        // Maksymalnie pokazanie początkowe + 2 ponowienia.
        if (sharedPrefs.getNotificationPromptCount() >= NotificationConsentConfig.MAX_PROMPTS) return false

        // Odstęp co najmniej 7 dni między pokazaniami.
        val lastPromptDate = sharedPrefs.getLastNotificationPromptDate()
        if (lastPromptDate != 0L) {
            val daysSinceLastPrompt = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastPromptDate)
            if (daysSinceLastPrompt < NotificationConsentConfig.MIN_DAYS_BETWEEN_PROMPTS) return false
        }

        return true
    }
}

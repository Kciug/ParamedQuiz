package com.rafalskrzypczyk.core.domain.use_cases

import com.rafalskrzypczyk.core.domain.config.AppRatingConfig
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CheckAppRatingEligibilityUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke(): Boolean {
        if (sharedPrefs.isAppRated() || sharedPrefs.isRatingPromptDisabled()) return false

        val installDate = sharedPrefs.getInstallDate()
        val daysSinceInstall = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - installDate)
        if (daysSinceInstall < AppRatingConfig.MIN_DAYS_SINCE_INSTALL) return false

        val completedQuizzes = sharedPrefs.getCompletedQuizzesCount()
        if (completedQuizzes < AppRatingConfig.MIN_COMPLETED_QUIZZES) return false

        val lastPromptDate = sharedPrefs.getLastRatingPromptDate()
        val daysSinceLastPrompt = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastPromptDate)
        if (daysSinceLastPrompt < AppRatingConfig.MIN_DAYS_BETWEEN_PROMPTS) return false

        return true
    }
}

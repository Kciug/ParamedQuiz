package com.rafalskrzypczyk.core.domain.use_cases

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
        if (daysSinceInstall < 3) return false

        val completedQuizzes = sharedPrefs.getCompletedQuizzesCount()
        if (completedQuizzes < 5) return false

        val lastPromptDate = sharedPrefs.getLastRatingPromptDate()
        val daysSinceLastPrompt = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastPromptDate)
        if (daysSinceLastPrompt < 14) return false

        return true
    }
}

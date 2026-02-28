package com.rafalskrzypczyk.paramedquiz.dev

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DevVM @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
): ViewModel() {

    fun onEvent(event: DevOptionsUIEvents) {
        when(event) {
            DevOptionsUIEvents.ResetOnboarding -> resetOnboarding()
            DevOptionsUIEvents.ResetModularOnboarding -> resetModularOnboarding()
            DevOptionsUIEvents.ClearTermsAcceptance -> clearTerms()
            DevOptionsUIEvents.ResetRatingStats -> resetRatingStats()
            DevOptionsUIEvents.TriggerRatingPrompt -> triggerRatingPrompt()
        }
    }

    private fun resetOnboarding() {
        sharedPreferences.setOnboardingStatus(false)
    }

    private fun resetModularOnboarding() {
        sharedPreferences.resetModularOnboarding()
    }

    private fun clearTerms() {
        sharedPreferences.setAcceptedTermsVersion(-1)
    }

    private fun resetRatingStats() {
        sharedPreferences.setAppRated(false)
        sharedPreferences.setLastRatingPromptDate(0L)
        sharedPreferences.setInstallDate(System.currentTimeMillis())
        sharedPreferences.resetCompletedQuizzesCount()
    }

    private fun triggerRatingPrompt() {
        val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        sharedPreferences.setInstallDate(sevenDaysAgo)
        sharedPreferences.setAppRated(false)
        sharedPreferences.setLastRatingPromptDate(0L)
        
        sharedPreferences.resetCompletedQuizzesCount()
        repeat(5) {
            sharedPreferences.incrementCompletedQuizzesCount()
        }
    }
}

package com.rafalskrzypczyk.paramedquiz.dev

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.lifecycle.HiltViewModel
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
}
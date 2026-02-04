package com.rafalskrzypczyk.translation_mode.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TranslationModeOnboardingVM @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) : ViewModel() {

    fun finishOnboarding(onSuccess: () -> Unit) {
        sharedPreferences.setTranslationModeOnboardingSeen(true)
        onSuccess()
    }
}

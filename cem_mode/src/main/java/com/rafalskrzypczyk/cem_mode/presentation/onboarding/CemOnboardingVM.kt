package com.rafalskrzypczyk.cem_mode.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.analyticsName
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.utils.QuizMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CemOnboardingVM @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    fun finishOnboarding(onSuccess: () -> Unit) {
        sharedPreferences.setCemModeOnboardingSeen(true)
        analyticsLogger.log(AnalyticsEvent.ModeOnboardingFinished(QuizMode.CemMode.analyticsName()))
        onSuccess()
    }
}

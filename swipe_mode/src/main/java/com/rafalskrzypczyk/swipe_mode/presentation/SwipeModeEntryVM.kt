package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SwipeModeEntryVM @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) : ViewModel() {
    
    fun shouldShowOnboarding(): Boolean {
        return !sharedPreferences.getSwipeModeOnboardingSeen()
    }
}

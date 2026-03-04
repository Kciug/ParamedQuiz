package com.rafalskrzypczyk.cem_mode.presentation

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CemModeEntryVM @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) : ViewModel() {

    fun isOnboardingSeen(): Boolean = sharedPreferences.getCemModeOnboardingSeen()
}

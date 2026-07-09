package com.rafalskrzypczyk.core.domain.use_cases

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

/** Terminalnie wyłącza pre-prompt powiadomień (np. po odmowie w dialogu systemowym). */
class DisableNotificationPromptUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke() = sharedPrefs.setNotificationPromptDisabled(true)
}

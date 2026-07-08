package com.rafalskrzypczyk.core.domain.use_cases

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

/** Rejestruje pokazanie pre-promptu powiadomień: licznik + znacznik czasu. */
class MarkNotificationPromptShownUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke() {
        sharedPrefs.incrementNotificationPromptCount()
        sharedPrefs.setLastNotificationPromptDate(System.currentTimeMillis())
    }
}

package com.rafalskrzypczyk.core.domain.use_cases

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

/** Ustawia aplikacyjną flagę powiadomień (włączenie po przyznaniu zgody). */
class SetNotificationsEnabledUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke(enabled: Boolean) = sharedPrefs.setNotificationsEnabled(enabled)
}

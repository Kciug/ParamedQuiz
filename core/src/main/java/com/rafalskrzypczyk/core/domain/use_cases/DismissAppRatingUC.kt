package com.rafalskrzypczyk.core.domain.use_cases

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

class DismissAppRatingUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke() = sharedPrefs.setLastRatingPromptDate(System.currentTimeMillis())
}

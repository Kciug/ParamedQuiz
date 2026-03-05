package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

class ResetSeenNewsUC @Inject constructor(
    private val sharedPreferencesApi: SharedPreferencesApi
) {
    operator fun invoke() = sharedPreferencesApi.clearSeenNewsIds()
}

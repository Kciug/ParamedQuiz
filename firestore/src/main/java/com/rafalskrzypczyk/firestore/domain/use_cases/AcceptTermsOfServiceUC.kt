package com.rafalskrzypczyk.firestore.domain.use_cases

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject

class AcceptTermsOfServiceUC @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke(version: Int) {
        sharedPrefs.setAcceptedTermsVersion(version)
    }
}

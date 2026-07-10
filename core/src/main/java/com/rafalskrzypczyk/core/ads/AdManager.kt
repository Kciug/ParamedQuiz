package com.rafalskrzypczyk.core.ads

import android.app.Activity

interface AdManager {
    fun initialize(activity: Activity)
    fun showInterstitial(activity: Activity, onAdShown: () -> Unit, onAdDismissed: () -> Unit)

    /** Debug/testowe: czyści stan zgody UMP, żeby formularz pojawił się przy następnym starcie. */
    fun resetConsent()
}

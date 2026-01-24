package com.rafalskrzypczyk.core.ads

import android.app.Activity

interface AdManager {
    fun initialize(activity: Activity)
    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit)
}

package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import android.app.Activity
import com.rafalskrzypczyk.core.ads.AdManager

/**
 * AdManager bez AdMob — w testach reklamy są zawsze pomijane. `showInterstitial` natychmiast
 * „pokazuje" i „zamyka" reklamę, żeby przepływ quizu przechodził dalej bez realnej reklamy.
 */
class NoOpAdManager : AdManager {
    override fun initialize(activity: Activity) = Unit

    override fun showInterstitial(activity: Activity, onAdShown: () -> Unit, onAdDismissed: () -> Unit) {
        onAdShown()
        onAdDismissed()
    }

    override fun resetConsent() = Unit
}

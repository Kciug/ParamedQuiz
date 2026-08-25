package com.rafalskrzypczyk.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rafalskrzypczyk.core.ads.AdManager
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val premiumStatusProvider: PremiumStatusProvider,
    private val gameplayConfig: GameplayConfigProvider,
    externalScope: CoroutineScope
) : AdManager {

    private val consentManager = GoogleMobileAdsConsentManager(context)
    private var interstitialAd: InterstitialAd? = null

    private val adUnitId = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID

    private var isAdsFree = false
    private var isMobileAdsInitialized = false

    init {
        externalScope.launch {
            premiumStatusProvider.isAdsFree.collect { free ->
                isAdsFree = free
                if (free) {
                    interstitialAd = null
                }
            }
        }
    }

    override fun resetConsent() {
        consentManager.reset()
    }

    /** Premium/„brak reklam" albo globalny wyłącznik z Remote Config (akcje promocyjne). */
    private fun areAdsBlocked() = isAdsFree || !gameplayConfig.adsEnabled()

    override fun initialize(activity: Activity) {
        // Zgodę zbieramy zawsze — dzięki temu po zakończeniu promocji reklamy wracają
        // od razu, bez czekania na kolejny start aplikacji.
        consentManager.gatherConsent(activity) { _ ->
            if (consentManager.canRequestAds && !areAdsBlocked()) {
                ensureMobileAdsInitialized()
                loadInterstitial()
            }
        }
    }

    private fun ensureMobileAdsInitialized() {
        if (isMobileAdsInitialized) return
        isMobileAdsInitialized = true
        MobileAds.initialize(context) {}
    }

    private fun loadInterstitial() {
        if (areAdsBlocked() || !consentManager.canRequestAds) return

        ensureMobileAdsInitialized()

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                if (areAdsBlocked()) {
                    interstitialAd = null
                    return
                }
                interstitialAd = ad
            }
        })
    }

    override fun showInterstitial(activity: Activity, onAdShown: () -> Unit, onAdDismissed: () -> Unit) {
        if (areAdsBlocked()) {
            onAdDismissed()
            return
        }

        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial() // Preload next one
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    onAdShown()
                    interstitialAd = null 
                }
            }
            interstitialAd?.show(activity)
        } else {
            loadInterstitial() // Try to load for next time
            onAdDismissed()
        }
    }
}
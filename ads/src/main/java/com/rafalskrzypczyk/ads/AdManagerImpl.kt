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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AdManager {

    private val consentManager = GoogleMobileAdsConsentManager(context)
    private var interstitialAd: InterstitialAd? = null
    
    // Test ID for Interstitial
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"
    
    private var showCount = 0
    private val frequency = 2

    override fun initialize(activity: Activity) {
        consentManager.gatherConsent(activity) { _ ->
            if (consentManager.canRequestAds) {
                MobileAds.initialize(context) {}
                loadInterstitial()
            }
        }
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }
        })
    }

    override fun showInterstitial(activity: Activity, onAdShown: () -> Unit, onAdDismissed: () -> Unit) {
        showCount++
        
        if (showCount % frequency == 0) {
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

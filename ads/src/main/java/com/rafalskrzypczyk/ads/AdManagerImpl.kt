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
import com.rafalskrzypczyk.core.analytics.AdStage
import com.rafalskrzypczyk.core.analytics.AdUnit
import com.rafalskrzypczyk.core.analytics.AnalyticsConsentManager
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
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
    private val analyticsConsentManager: AnalyticsConsentManager,
    private val analyticsLogger: AnalyticsLogger,
    private val tcfConsentReader: TcfConsentReader,
    externalScope: CoroutineScope
) : AdManager {

    private val consentManager = GoogleMobileAdsConsentManager(context)
    private var interstitialAd: InterstitialAd? = null

    private val adUnitId = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID

    /**
     * Jednostki testowe Google maja staly wydawce; w debug i staging `local.defaults.properties`
     * podaje wlasnie je. Bez tego podzialu odslony testowe weszlyby w dane produkcyjne.
     */
    private val adUnit =
        if (adUnitId.startsWith(GOOGLE_TEST_PUBLISHER)) AdUnit.TEST else AdUnit.PRODUCTION

    /**
     * Liczba odpowiedzi od poprzedniej reklamy, przekazana przez QuizAdHandler w chwili decyzji.
     * Reklama startowa (preload przy inicjalizacji) nie ma zadnej sesji quizu, wiec zostaje zero.
     */
    private var answersSinceLastAd = 0

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

    override fun onInterstitialTriggered(answersSinceLastAd: Int) {
        this.answersSinceLastAd = answersSinceLastAd
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
            // Jedyny moment, w którym zgoda jest ustalona. Ustawiamy ją poza gałęzią poniżej,
            // żeby propagacja objęła też użytkowników premium i okres wyłączonych reklam.
            analyticsConsentManager.updateAdConsent(tcfConsentReader.read(consentManager.canRequestAds))

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
                logAdFailure(AdStage.LOAD, adError.code)
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

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    logAdFailure(AdStage.PRESENT, adError.code)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    // Dopiero tutaj reklama jest naprawde na ekranie — show() samo tego nie gwarantuje.
                    analyticsLogger.log(AnalyticsEvent.AdShown(adUnit, answersSinceLastAd))
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

    private fun logAdFailure(stage: AdStage, errorCode: Int) {
        analyticsLogger.log(
            AnalyticsEvent.AdLoadFailed(
                stage = stage,
                errorCode = adErrorName(stage, errorCode),
                adUnit = adUnit,
            )
        )
    }

    /**
     * Kody AdMob jako nazwy — wspolny slownik z iOS, nieznane jako `code_<n>`. Przestrzenie kodow
     * ladowania i prezentacji nakladaja sie numerycznie (0 to blad wewnetrzny w obu, ale 1 to raz
     * bledne zadanie, raz ponowne uzycie reklamy), stad rozgalezienie po [stage].
     */
    private fun adErrorName(stage: AdStage, code: Int): String = when (stage) {
        AdStage.LOAD -> when (code) {
            AdRequest.ERROR_CODE_INTERNAL_ERROR -> "internal_error"
            AdRequest.ERROR_CODE_INVALID_REQUEST -> "invalid_request"
            AdRequest.ERROR_CODE_NETWORK_ERROR -> "network_error"
            AdRequest.ERROR_CODE_NO_FILL -> "no_fill"
            AdRequest.ERROR_CODE_APP_ID_MISSING -> "app_id_missing"
            AdRequest.ERROR_CODE_MEDIATION_NO_FILL -> "mediation_no_fill"
            else -> "code_$code"
        }
        AdStage.PRESENT -> when (code) {
            FullScreenContentCallback.ERROR_CODE_INTERNAL_ERROR -> "internal_error"
            FullScreenContentCallback.ERROR_CODE_AD_REUSED -> "ad_reused"
            FullScreenContentCallback.ERROR_CODE_NOT_READY -> "not_ready"
            FullScreenContentCallback.ERROR_CODE_APP_NOT_FOREGROUND -> "app_not_foreground"
            FullScreenContentCallback.ERROR_CODE_MEDIATION_SHOW_ERROR -> "mediation_show_error"
            else -> "code_$code"
        }
    }

    private companion object {
        const val GOOGLE_TEST_PUBLISHER = "ca-app-pub-3940256099942544"
    }
}
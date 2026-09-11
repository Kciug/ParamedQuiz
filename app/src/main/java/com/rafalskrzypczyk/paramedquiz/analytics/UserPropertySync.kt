package com.rafalskrzypczyk.paramedquiz.analytics

import android.content.SharedPreferences
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsUserProperty
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.core.error.CrashReporter
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesService
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.score.domain.ScoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utrzymuje właściwości użytkownika w zgodzie ze stanem aplikacji.
 *
 * Nie jest to jednorazowa migawka w `onCreate`: premium startuje pustym zbiorem, seria zerem, a
 * wartości domykają się dopiero po odpowiedzi Firestore/Play. Jednorazowy odczyt raportowałby
 * `premium_tier = none` dla każdego płacącego przez pierwsze sekundy każdej sesji.
 */
@Singleton
class UserPropertySync @Inject constructor(
    private val analyticsLogger: AnalyticsLogger,
    private val premiumStatusProvider: PremiumStatusProvider,
    private val gameplayConfig: GameplayConfigProvider,
    private val crashReporter: CrashReporter,
    private val userManager: UserManager,
    private val sharedPreferences: SharedPreferencesApi,
    private val rawSharedPreferences: SharedPreferences,
    private val scoreManager: ScoreManager,
    private val externalScope: CoroutineScope,
) {
    /**
     * Trzymany w polu, bo [SharedPreferences] przechowuje listenery przez slabe referencje —
     * lokalna zmienna zostalaby zebrana i wlasciwosci znow by sie zestarzaly.
     */
    private val preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SharedPreferencesService.KEY_CURRENT_USER ||
                key == SharedPreferencesService.KEY_NOTIFICATIONS_ENABLED
            ) {
                refreshLocalState()
            }
        }

    private var isStarted = false

    /**
     * Ostatnie znane wartosci z kolektorow. Bez nich [syncAll] nie mialby czego odtworzyc:
     * kolektory maja `distinctUntilChanged`, wiec po wycofaniu i ponownym udzieleniu zgody
     * nie powtorza niezmienionej wartosci.
     */
    private var lastPremiumTier: String? = null
    private var lastAdsDisabled: String? = null
    private var lastStreakBucket: String? = null

    /**
     * Wolane przy KAZDYM wejsciu zgody w stan udzielonej, nie tylko przy pierwszym.
     * Wycofanie zgody wola `resetAnalyticsData()`, ktore kasuje app-instance-id razem z
     * wlasciwosciami uzytkownika — po ponownej zgodzie mamy wiec nowa tozsamosc, ktora bez
     * [syncAll] zostalaby bez zadnej segmentacji do konca zycia procesu.
     */
    fun onConsentGranted() {
        if (!isStarted) {
            isStarted = true
            // Logowanie, wylogowanie i przelacznik powiadomien nie maja flow — obserwujemy wiec
            // ich magazyn, inaczej obie wlasciwosci zamarzalyby na stanie ze startu procesu.
            rawSharedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener)
            startCollectors()
        }
        syncAll()
    }

    private fun syncAll() {
        // Typ builda ustawiamy tutaj, a nie przy budowie grafu DI: wariant staging ma ten sam
        // applicationId co release, a wlasciwosc zapisana przy wylaczonym zbieraniu przepada.
        val buildType = com.rafalskrzypczyk.analytics.BuildConfig.BUILD_TYPE_NAME
        analyticsLogger.setUserProperty(AnalyticsUserProperty.BUILD_TYPE, buildType)
        // Ta sama wartosc w Crashlytics: staging dzieli applicationId z produkcja, wiec bez
        // tego klucza crashe z internal tracka mieszaja sie z produkcyjnymi.
        crashReporter.setCustomKey(AnalyticsUserProperty.BUILD_TYPE.propertyName, buildType)
        refreshLocalState()
        lastPremiumTier?.let { analyticsLogger.setUserProperty(AnalyticsUserProperty.PREMIUM_TIER, it) }
        lastAdsDisabled?.let { analyticsLogger.setUserProperty(AnalyticsUserProperty.ADS_DISABLED, it) }
        lastStreakBucket?.let { analyticsLogger.setUserProperty(AnalyticsUserProperty.STREAK_BUCKET, it) }
    }

    private fun startCollectors() {
        externalScope.launch {
            premiumStatusProvider.ownedProductIds
                .map { it.toPremiumTier() }
                .distinctUntilChanged()
                .collect {
                    lastPremiumTier = it
                    analyticsLogger.setUserProperty(AnalyticsUserProperty.PREMIUM_TIER, it)
                }
        }

        externalScope.launch {
            premiumStatusProvider.isAdsFree
                .map { adsFree -> adsFree || !gameplayConfig.adsEnabled() }
                .distinctUntilChanged()
                .collect {
                    lastAdsDisabled = it.toString()
                    analyticsLogger.setUserProperty(AnalyticsUserProperty.ADS_DISABLED, it.toString())
                }
        }

        externalScope.launch {
            scoreManager.getScoreFlow()
                .map { it.streak.toStreakBucket() }
                .distinctUntilChanged()
                .collect {
                    lastStreakBucket = it
                    analyticsLogger.setUserProperty(AnalyticsUserProperty.STREAK_BUCKET, it)
                }
        }
    }

    /** Wartości czytane synchronicznie (brak flow), więc odświeżane na żądanie. */
    fun refreshLocalState() {
        val isLoggedIn = userManager.getCurrentLoggedUser() != null
        analyticsLogger.setUserProperty(AnalyticsUserProperty.IS_LOGGED_IN, isLoggedIn.toString())
        analyticsLogger.setUserProperty(
            AnalyticsUserProperty.NOTIFICATIONS_ON,
            sharedPreferences.isNotificationsEnabled().toString(),
        )
    }

    private fun Set<String>.toPremiumTier(): String = when {
        contains(BillingIds.ID_FULL_PACKAGE) -> TIER_FULL
        isEmpty() -> TIER_NONE
        else -> TIER_PARTIAL
    }

    private fun Int.toStreakBucket(): String = when {
        this <= 0 -> "0"
        this <= 3 -> "1_3"
        this <= 7 -> "4_7"
        this <= 30 -> "8_30"
        else -> "30_plus"
    }

    private companion object {
        const val TIER_NONE = "none"
        const val TIER_PARTIAL = "partial"
        const val TIER_FULL = "full"
    }
}

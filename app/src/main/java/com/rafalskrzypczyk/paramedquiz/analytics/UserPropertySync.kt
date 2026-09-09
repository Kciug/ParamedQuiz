package com.rafalskrzypczyk.paramedquiz.analytics

import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsUserProperty
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
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
    private val userManager: UserManager,
    private val sharedPreferences: SharedPreferencesApi,
    private val scoreManager: ScoreManager,
    private val externalScope: CoroutineScope,
) {
    fun start() {
        refreshLocalState()

        externalScope.launch {
            premiumStatusProvider.ownedProductIds
                .map { it.toPremiumTier() }
                .distinctUntilChanged()
                .collect { analyticsLogger.setUserProperty(AnalyticsUserProperty.PREMIUM_TIER, it) }
        }

        externalScope.launch {
            premiumStatusProvider.isAdsFree
                .map { adsFree -> adsFree || !gameplayConfig.adsEnabled() }
                .distinctUntilChanged()
                .collect {
                    analyticsLogger.setUserProperty(AnalyticsUserProperty.ADS_DISABLED, it.toString())
                }
        }

        externalScope.launch {
            scoreManager.getScoreFlow()
                .map { it.streak.toStreakBucket() }
                .distinctUntilChanged()
                .collect { analyticsLogger.setUserProperty(AnalyticsUserProperty.STREAK_BUCKET, it) }
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

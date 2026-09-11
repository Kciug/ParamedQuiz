package com.rafalskrzypczyk.paramedquiz

import android.app.Application
import com.rafalskrzypczyk.ads.TcfConsentReader
import com.rafalskrzypczyk.billing.analytics.PurchaseFunnelTracker
import com.rafalskrzypczyk.core.analytics.AnalyticsConsentManager
import com.rafalskrzypczyk.core.analytics.AnalyticsConsentState
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.notifications.ContentTopicManager
import com.rafalskrzypczyk.notifications.NotificationChannels
import com.rafalskrzypczyk.notifications.ReminderScheduler
import com.rafalskrzypczyk.paramedquiz.analytics.UserPropertySync
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ParamedQuizApplication : Application() {
    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var contentTopicManager: ContentTopicManager

    @Inject
    lateinit var gameplayConfig: GameplayConfigProvider

    @Inject
    lateinit var analyticsConsentManager: AnalyticsConsentManager

    @Inject
    lateinit var tcfConsentReader: TcfConsentReader

    @Inject
    lateinit var userPropertySync: UserPropertySync

    @Inject
    lateinit var purchaseFunnelTracker: PurchaseFunnelTracker

    override fun onCreate() {
        super.onCreate()

        // Musi byc pierwsze: dopiero to wlacza albo wylacza zbieranie zgodnie z zapisana
        // decyzja uzytkownika. Stan zgod reklamowych zasiewamy z zapisanych ciagow TCF, zeby
        // uzytkownik, ktory juz przeszedl formularz UMP, nie dostawal odmowy przy kazdym starcie.
        analyticsConsentManager.apply(tcfConsentReader.read(canRequestAds = false))

        // Wlasciwosci uzytkownika maja sens dopiero po zgodzie: ustawione przy wylaczonym SDK
        // sa gubione, a kolektory maja distinctUntilChanged i nie wyemitowalyby ponownie.
        // Kolektor jest ciagly, a nie jednorazowy: przelacznik w ustawieniach pozwala wycofac
        // i ponownie udzielic zgody w tej samej sesji, a wycofanie kasuje app-instance-id.
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            analyticsConsentManager.state
                .filter { it == AnalyticsConsentState.GRANTED }
                .collect { userPropertySync.onConsentGranted() }
        }
        // Musi wystartowac tutaj: purchaseResult nie ma replay, wiec wynik zakupu
        // wyemitowany przed subskrypcja przepada.
        purchaseFunnelTracker.start()

        NotificationChannels.ensureCreated(this)
        reminderScheduler.ensureScheduled()
        contentTopicManager.ensureSubscription()

        // Odświeżenie gameplay configu z Remote Config (bramka TTL po stronie SDK, ciche na błędzie).
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            gameplayConfig.refresh()
        }
    }
}

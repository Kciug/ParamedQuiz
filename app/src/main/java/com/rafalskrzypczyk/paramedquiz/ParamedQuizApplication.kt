package com.rafalskrzypczyk.paramedquiz

import android.app.Application
import com.rafalskrzypczyk.ads.TcfConsentReader
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.notifications.ContentTopicManager
import com.rafalskrzypczyk.notifications.NotificationChannels
import com.rafalskrzypczyk.notifications.ReminderScheduler
import com.rafalskrzypczyk.paramedquiz.analytics.UserPropertySync
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    lateinit var analyticsLogger: AnalyticsLogger

    @Inject
    lateinit var tcfConsentReader: TcfConsentReader

    @Inject
    lateinit var userPropertySync: UserPropertySync

    override fun onCreate() {
        super.onCreate()

        // Zgoda z zapisanego stanu TCF, zanim poleci pierwsze zdarzenie. Formularz UMP
        // (o ile jest wymagany) doprecyzuje ją przy starcie MainActivity.
        analyticsLogger.setConsent(tcfConsentReader.read(canRequestAds = false))
        userPropertySync.start()

        NotificationChannels.ensureCreated(this)
        reminderScheduler.ensureScheduled()
        contentTopicManager.ensureSubscription()

        // Odświeżenie gameplay configu z Remote Config (bramka TTL po stronie SDK, ciche na błędzie).
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            gameplayConfig.refresh()
        }
    }
}

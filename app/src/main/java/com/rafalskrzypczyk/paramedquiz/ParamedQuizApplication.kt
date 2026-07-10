package com.rafalskrzypczyk.paramedquiz

import android.app.Application
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.notifications.ContentTopicManager
import com.rafalskrzypczyk.notifications.NotificationChannels
import com.rafalskrzypczyk.notifications.ReminderScheduler
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

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensureCreated(this)
        reminderScheduler.ensureScheduled()
        contentTopicManager.ensureSubscription()

        // Odświeżenie gameplay configu z Remote Config (bramka TTL po stronie SDK, ciche na błędzie).
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            gameplayConfig.refresh()
        }
    }
}

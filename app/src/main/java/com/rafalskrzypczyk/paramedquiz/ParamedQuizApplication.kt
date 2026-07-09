package com.rafalskrzypczyk.paramedquiz

import android.app.Application
import com.rafalskrzypczyk.notifications.NotificationChannels
import com.rafalskrzypczyk.notifications.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ParamedQuizApplication : Application() {
    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensureCreated(this)
        reminderScheduler.ensureScheduled()
    }
}

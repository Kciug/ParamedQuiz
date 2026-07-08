package com.rafalskrzypczyk.paramedquiz

import android.app.Application
import com.rafalskrzypczyk.notifications.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ParamedQuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensureCreated(this)
    }
}

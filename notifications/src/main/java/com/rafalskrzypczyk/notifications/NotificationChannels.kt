package com.rafalskrzypczyk.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

object NotificationChannels {
    const val REMINDERS_CHANNEL_ID = "reminders"
    const val NEWS_CHANNEL_ID = "news"

    /**
     * Tworzy kanały powiadomień (idempotentne — bezpieczne do wielokrotnego wywołania).
     * Wołane przy starcie aplikacji oraz defensywnie przed wysłaniem powiadomienia.
     */
    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return

        if (manager.getNotificationChannel(REMINDERS_CHANNEL_ID) == null) {
            val reminders = NotificationChannel(
                REMINDERS_CHANNEL_ID,
                context.getString(R.string.notification_channel_reminders_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_reminders_desc)
            }
            manager.createNotificationChannel(reminders)
        }

        if (manager.getNotificationChannel(NEWS_CHANNEL_ID) == null) {
            val news = NotificationChannel(
                NEWS_CHANNEL_ID,
                context.getString(R.string.notification_channel_news_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_news_desc)
            }
            manager.createNotificationChannel(news)
        }
    }
}

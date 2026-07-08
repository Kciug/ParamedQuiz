package com.rafalskrzypczyk.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Buduje i wyświetla powiadomienia lokalne.
 *
 * Celowo nie zależy od modułu :app — cel po tapnięciu przekazujemy jako extra w launch-intencie
 * ([NotificationDestination.EXTRA_DESTINATION]), a MainActivity mapuje go na właściwą trasę.
 */
@Singleton
class Notifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission") // chronione przez areNotificationsEnabled()
    fun show(
        notificationId: Int,
        title: String,
        text: String,
        destination: NotificationDestination
    ) {
        if (!NotificationPermission.areNotificationsEnabled(context)) return

        NotificationChannels.ensureCreated(context)

        val notification = NotificationCompat.Builder(context, NotificationChannels.REMINDERS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setContentIntent(buildContentIntent(destination))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun buildContentIntent(destination: NotificationDestination): PendingIntent {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(NotificationDestination.EXTRA_DESTINATION, destination.name)
            }

        return PendingIntent.getActivity(
            context,
            destination.ordinal,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

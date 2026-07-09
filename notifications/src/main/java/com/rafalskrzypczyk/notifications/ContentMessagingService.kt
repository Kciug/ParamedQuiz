package com.rafalskrzypczyk.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Zależności serwisu wstrzykiwane przez Hilt [EntryPoint] (zamiast @AndroidEntryPoint) — analogicznie
 * do [ReminderWorkerEntryPoint], by agregacja Hilta w :app nie musiała rozwiązywać FirebaseMessagingService
 * (firebase-messaging jest `implementation` w :notifications). Interfejs celowo top-level.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContentMessagingEntryPoint {
    fun notifier(): Notifier
    fun sharedPrefs(): SharedPreferencesApi
    fun contentTopicManager(): ContentTopicManager
}

/**
 * Odbiera powiadomienia Content/Marketing przez FCM (temat "content").
 * W tle system wyświetla notyfikację sam; tutaj obsługujemy foreground (oraz data-only),
 * budując powiadomienie przez [Notifier]. Realną bramką zgody jest subskrypcja tematu
 * ([ContentTopicManager]) — w tle nie da się bramkować na flagę usera.
 */
class ContentMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Model tematowy — nie przechowujemy tokenu. Subskrypcje tematów przeżywają refresh tokenu,
        // ale defensywnie odświeżamy je jawnie (idempotentne).
        EntryPointAccessors.fromApplication(applicationContext, ContentMessagingEntryPoint::class.java)
            .contentTopicManager()
            .ensureSubscription()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val deps = EntryPointAccessors.fromApplication(applicationContext, ContentMessagingEntryPoint::class.java)
        if (!deps.sharedPrefs().isNotificationsEnabled()) return

        val data = message.data
        val title = message.notification?.title
            ?: data["title"]
            ?: applicationInfo.loadLabel(packageManager).toString()
        val body = message.notification?.body ?: data["body"] ?: return

        val destination = NotificationDestination.fromExtra(data[NotificationDestination.EXTRA_DESTINATION])
            ?: NotificationDestination.HOME

        // Kategoria = kanał; z payloadu (channel_id lub data["channel"]), domyślnie „news".
        val requestedChannel = message.notification?.channelId ?: data["channel"]
        val (channelId, notificationId) = if (requestedChannel == NotificationChannels.MARKETING_CHANNEL_ID) {
            NotificationChannels.MARKETING_CHANNEL_ID to NotificationIds.MARKETING
        } else {
            NotificationChannels.NEWS_CHANNEL_ID to NotificationIds.NEWS
        }

        deps.notifier().show(
            notificationId = notificationId,
            title = title,
            text = body,
            destination = destination,
            channelId = channelId
        )
    }
}

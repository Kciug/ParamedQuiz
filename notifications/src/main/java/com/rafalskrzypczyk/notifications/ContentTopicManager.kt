package com.rafalskrzypczyk.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Steruje subskrypcją tematów FCM per kategoria (`news`, `marketing`). Subskrypcja jest **główną bramką
 * zgody** na powiadomienia zdalne (w tle nie da się bramkować na flagę per-wiadomość).
 *
 * Wymaga, by backend/wysyłka publikowała na tematy `news` / `marketing` (zamiast dawnego wspólnego
 * `content`). Stary temat `content` jest jednorazowo odsubskrybowywany (migracja).
 */
@Singleton
class ContentTopicManager @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) {
    /**
     * Uzgadnia subskrypcje: temat danej kategorii jest zasubskrybowany, gdy powiadomienia są włączone
     * i dana kategoria jest włączona; w przeciwnym razie odsubskrybowany.
     */
    fun ensureSubscription() {
        val messaging = FirebaseMessaging.getInstance()
        val notificationsEnabled = sharedPreferences.isNotificationsEnabled()

        setSubscription(messaging, NEWS_TOPIC, notificationsEnabled && sharedPreferences.isNewsEnabled())
        setSubscription(messaging, MARKETING_TOPIC, notificationsEnabled && sharedPreferences.isMarketingEnabled())

        // Migracja: odsubskrybuj dawny wspólny temat, by nie dostawać legacy delivery.
        messaging.unsubscribeFromTopic(LEGACY_CONTENT_TOPIC)
    }

    private fun setSubscription(messaging: FirebaseMessaging, topic: String, subscribed: Boolean) {
        if (subscribed) {
            messaging.subscribeToTopic(topic)
        } else {
            messaging.unsubscribeFromTopic(topic)
        }
    }

    companion object {
        private const val NEWS_TOPIC = "news"
        private const val MARKETING_TOPIC = "marketing"
        private const val LEGACY_CONTENT_TOPIC = "content"
    }
}

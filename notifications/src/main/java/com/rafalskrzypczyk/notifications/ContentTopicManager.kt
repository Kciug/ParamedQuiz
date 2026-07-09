package com.rafalskrzypczyk.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Steruje subskrypcją tematu FCM Content/Marketing. Subskrypcja jest **główną bramką zgody**
 * na powiadomienia zdalne (w tle nie da się bramkować na flagę per-wiadomość).
 */
@Singleton
class ContentTopicManager @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) {
    /** Subskrybuje temat, gdy powiadomienia są włączone; w przeciwnym razie odsubskrybowuje. */
    fun ensureSubscription() {
        val messaging = FirebaseMessaging.getInstance()
        if (sharedPreferences.isNotificationsEnabled()) {
            messaging.subscribeToTopic(CONTENT_TOPIC)
        } else {
            messaging.unsubscribeFromTopic(CONTENT_TOPIC)
        }
    }

    companion object {
        private const val CONTENT_TOPIC = "content"
    }
}

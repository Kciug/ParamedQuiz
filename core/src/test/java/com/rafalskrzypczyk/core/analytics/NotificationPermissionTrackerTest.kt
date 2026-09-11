package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Android pokazuje dialog `POST_NOTIFICATIONS` najwyzej dwa razy, a po trwalej odmowie oddaje
 * `false` natychmiast. Bez bramki kazde tapniecie przelacznika w ustawieniach produkowaloby
 * falszywe `granted = 0`, a prompt na ekranie glownym jest uprawniony do trzech wyswietlen.
 */
class NotificationPermissionTrackerTest {

    private lateinit var analytics: RecordingAnalyticsLogger
    private lateinit var sharedPreferences: SharedPreferencesApi
    private lateinit var tracker: NotificationPermissionTracker

    private var asked = false

    @Before
    fun setUp() {
        analytics = RecordingAnalyticsLogger()
        sharedPreferences = mockk(relaxed = true)
        every { sharedPreferences.isNotificationPermissionAsked() } answers { asked }
        every { sharedPreferences.setNotificationPermissionAsked() } answers { asked = true }
        tracker = NotificationPermissionTracker(analytics, sharedPreferences)
    }

    @Test
    fun `the first answer is reported with its result`() {
        tracker.onSystemDialogAnswered(granted = true)

        val event = analytics.eventsOfType<AnalyticsEvent.NotificationPermissionAnswered>().single()
        assertTrue(event.granted)
        assertEquals(1L, event.params["granted"])
    }

    @Test
    fun `later answers are dropped, so a blocked dialog cannot fake a denial`() {
        tracker.onSystemDialogAnswered(granted = true)
        tracker.onSystemDialogAnswered(granted = false)
        tracker.onSystemDialogAnswered(granted = false)

        assertEquals(listOf("notification_permission"), analytics.eventNames())
    }

    @Test
    fun `a decision made on an earlier launch is not reported again`() {
        asked = true

        tracker.onSystemDialogAnswered(granted = true)

        assertTrue(analytics.events.isEmpty())
    }
}

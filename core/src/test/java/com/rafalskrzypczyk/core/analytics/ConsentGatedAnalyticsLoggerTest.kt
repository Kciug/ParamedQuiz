package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import com.rafalskrzypczyk.core.utils.TimeProvider
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Kontrakt cross-platform mówi, że przed zgodą nic nie jest logowane — nie „nic nie jest wysyłane".
 * Wyłączenie zbierania po stronie SDK nie powstrzymuje naszego kodu przed produkowaniem zdarzeń,
 * więc bramka jest jedynym miejscem, w którym ten niezmiennik da się sprawdzić.
 */
class ConsentGatedAnalyticsLoggerTest {

    private lateinit var recorder: RecordingAnalyticsLogger
    private lateinit var gate: AnalyticsCollectionGate
    private lateinit var logger: AnalyticsLogger

    @Before
    fun setUp() {
        recorder = RecordingAnalyticsLogger()
        gate = AnalyticsCollectionGate()
        logger = ConsentGatedAnalyticsLogger(recorder, gate)
    }

    @Test
    fun `nothing passes while the gate is closed`() {
        logger.log(AnalyticsEvent.RatingPromptViewed)
        logger.setUserProperty(AnalyticsUserProperty.IS_LOGGED_IN, "true")

        assertTrue(recorder.events.isEmpty())
        assertTrue(recorder.userProperties.isEmpty())
    }

    @Test
    fun `events pass once consent opens the gate`() {
        gate.isOpen = true

        logger.log(AnalyticsEvent.RatingPromptViewed)
        logger.setUserProperty(AnalyticsUserProperty.IS_LOGGED_IN, "true")

        assertEquals(listOf("rating_prompt_view"), recorder.eventNames())
        assertEquals("true", recorder.userProperties[AnalyticsUserProperty.IS_LOGGED_IN])
    }

    /**
     * Podglad w opcjach deweloperskich ma pokazywac to, co faktycznie wyszlo — pusta lista przy
     * wycofanej zgodzie jest testem bramki, nie bledem.
     */
    @Test
    fun `the recent events buffer is fed only behind the gate`() {
        val recent = RecentAnalyticsEvents(object : TimeProvider {
            override fun now(): Date = Date(0L)
        })
        val gated = ConsentGatedAnalyticsLogger(recorder, gate, recent)

        gated.log(AnalyticsEvent.RatingPromptViewed)
        gated.setUserProperty(AnalyticsUserProperty.IS_LOGGED_IN, "true")
        assertTrue(recent.entries.value.isEmpty())

        gate.isOpen = true
        gated.log(AnalyticsEvent.DevTestEvent)
        gated.setUserProperty(AnalyticsUserProperty.IS_LOGGED_IN, "true")

        val names = recent.entries.value.map {
            when (it) {
                is RecentAnalyticsEvents.Entry.Event -> it.name
                is RecentAnalyticsEvents.Entry.Property -> it.name
            }
        }
        assertEquals(listOf("is_logged_in", "dev_test_event"), names)
    }

    @Test
    fun `withdrawing consent closes the gate again`() {
        gate.isOpen = true
        logger.log(AnalyticsEvent.RatingPromptViewed)

        gate.isOpen = false
        logger.log(AnalyticsEvent.NotificationPromptViewed)

        assertEquals(listOf("rating_prompt_view"), recorder.eventNames())
    }
}

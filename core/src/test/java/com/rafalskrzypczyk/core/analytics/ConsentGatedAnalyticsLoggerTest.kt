package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
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

    @Test
    fun `withdrawing consent closes the gate again`() {
        gate.isOpen = true
        logger.log(AnalyticsEvent.RatingPromptViewed)

        gate.isOpen = false
        logger.log(AnalyticsEvent.NotificationPromptViewed)

        assertEquals(listOf("rating_prompt_view"), recorder.eventNames())
    }
}

package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.utils.TimeProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class RecentAnalyticsEventsTest {

    private var nowMillis = 1_000L
    private val buffer = RecentAnalyticsEvents(object : TimeProvider {
        override fun now(): Date = Date(nowMillis)
    })

    @Test
    fun `entries are kept newest first with their time and params`() {
        buffer.record(AnalyticsEvent.RatingPromptViewed)
        nowMillis = 2_000L
        buffer.record(AnalyticsEvent.TrialStarted("swipe"))
        nowMillis = 3_000L
        buffer.record(AnalyticsUserProperty.PREMIUM_TIER, "full")

        val entries = buffer.entries.value
        assertEquals(3, entries.size)

        val property = entries[0] as RecentAnalyticsEvents.Entry.Property
        assertEquals(3_000L, property.atMillis)
        assertEquals("premium_tier", property.name)
        assertEquals("full", property.value)

        val trial = entries[1] as RecentAnalyticsEvents.Entry.Event
        assertEquals("trial_start", trial.name)
        assertEquals(mapOf("mode" to "swipe"), trial.params)

        assertEquals("rating_prompt_view", (entries[2] as RecentAnalyticsEvents.Entry.Event).name)
    }

    @Test
    fun `the buffer drops the oldest entries beyond its capacity`() {
        repeat(RecentAnalyticsEvents.CAPACITY + 5) { index ->
            nowMillis = index.toLong()
            buffer.record(AnalyticsEvent.RatingPromptViewed)
        }

        val entries = buffer.entries.value
        assertEquals(RecentAnalyticsEvents.CAPACITY, entries.size)
        assertEquals((RecentAnalyticsEvents.CAPACITY + 4).toLong(), entries.first().atMillis)
        assertEquals(5L, entries.last().atMillis)
    }

    @Test
    fun `clear empties the buffer`() {
        buffer.record(AnalyticsEvent.RatingPromptViewed)

        buffer.clear()

        assertTrue(buffer.entries.value.isEmpty())
    }
}

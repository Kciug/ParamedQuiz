package com.rafalskrzypczyk.notifications

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ReminderDeciderTest {

    private val decider = ReminderDecider()
    private val now: Date = Date()

    private fun daysAgo(n: Int): Date =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -n) }.time

    @Test
    fun `done today returns None`() {
        val result = decider.decide(
            lastStreakUpdateDate = now,
            streak = 5,
            now = now,
            lastWinbackDaySent = 0
        )
        assertEquals(ReminderDecision.None, result)
    }

    @Test
    fun `pending with streak at or above min returns Streak`() {
        val result = decider.decide(
            lastStreakUpdateDate = daysAgo(1),
            streak = 5,
            now = now,
            lastWinbackDaySent = 0
        )
        assertEquals(ReminderDecision.Streak(5), result)
    }

    @Test
    fun `pending with streak below min falls back to Daily`() {
        val result = decider.decide(
            lastStreakUpdateDate = daysAgo(1),
            streak = 1,
            now = now,
            lastWinbackDaySent = 0
        )
        assertEquals(ReminderDecision.Daily, result)
    }

    @Test
    fun `seven days inactive returns Winback 7`() {
        val result = decider.decide(
            lastStreakUpdateDate = daysAgo(7),
            streak = 5,
            now = now,
            lastWinbackDaySent = 0
        )
        assertEquals(ReminderDecision.Winback(7), result)
    }

    @Test
    fun `fourteen days inactive returns Winback 14`() {
        val result = decider.decide(
            lastStreakUpdateDate = daysAgo(14),
            streak = 5,
            now = now,
            lastWinbackDaySent = 7
        )
        assertEquals(ReminderDecision.Winback(14), result)
    }

    @Test
    fun `winback not repeated for same threshold`() {
        val result = decider.decide(
            lastStreakUpdateDate = daysAgo(7),
            streak = 5,
            now = now,
            lastWinbackDaySent = 7
        )
        assertEquals(ReminderDecision.Daily, result)
    }

    @Test
    fun `inactive between thresholds returns Daily`() {
        val result = decider.decide(
            lastStreakUpdateDate = daysAgo(3),
            streak = 5,
            now = now,
            lastWinbackDaySent = 0
        )
        assertEquals(ReminderDecision.Daily, result)
    }

    @Test
    fun `never studied returns Daily`() {
        val result = decider.decide(
            lastStreakUpdateDate = null,
            streak = 0,
            now = now,
            lastWinbackDaySent = 0
        )
        assertEquals(ReminderDecision.Daily, result)
    }
}

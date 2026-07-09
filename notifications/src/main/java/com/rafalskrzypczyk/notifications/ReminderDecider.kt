package com.rafalskrzypczyk.notifications

import com.rafalskrzypczyk.core.utils.toDateOnly
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Czysta (bez zależności Android) logika decyzyjna „oceń i zdecyduj" — realizuje pseudokod spec §5.2.
 * Priorytet: DONE → nic; PENDING & streak≥min → Streak; Win-back (7/14) → Winback;
 * Revision (słabe pytania & interwał) → Revision; wpp → Daily.
 *
 * Stany liczone samodzielnie z [lastStreakUpdateDate] (bez StreakManager.getStreakState, który ma
 * efekt uboczny zerowania serii).
 */
class ReminderDecider {
    fun decide(
        lastStreakUpdateDate: Date?,
        streak: Int,
        now: Date,
        lastWinbackDaySent: Int,
        weakQuestionsCount: Int = 0,
        lastRevisionReminderDate: Long = 0L,
        streakMinValue: Int = DEFAULT_STREAK_MIN_VALUE,
        winbackDays: List<Int> = DEFAULT_WINBACK_DAYS,
        minWeakQuestions: Int = DEFAULT_MIN_WEAK_QUESTIONS,
        revisionIntervalDays: Int = DEFAULT_REVISION_INTERVAL_DAYS
    ): ReminderDecision {
        val daysSince = daysSinceLastDone(lastStreakUpdateDate, now)

        // DONE dziś — nic nie wysyłamy.
        if (daysSince == 0L) return ReminderDecision.None

        // Seria zagrożona (ostatnie zaliczenie wczoraj) i wystarczająco długa.
        if (daysSince == 1L && streak >= streakMinValue) {
            return ReminderDecision.Streak(streak)
        }

        // Win-back na dokładnym progu, jeśli nie wysłany już dla tego progu.
        val winbackDay = winbackDays.firstOrNull { it.toLong() == daysSince }
        if (winbackDay != null && winbackDay != lastWinbackDaySent) {
            return ReminderDecision.Winback(winbackDay)
        }

        // Revision — gdy jest co powtarzać i minął interwał od ostatniego przypomnienia.
        if (weakQuestionsCount >= minWeakQuestions &&
            daysSinceRevisionReminder(lastRevisionReminderDate, now) >= revisionIntervalDays
        ) {
            return ReminderDecision.Revision
        }

        return ReminderDecision.Daily
    }

    /** Pełne dni od ostatniego revision remindera; duża wartość gdy nigdy (0). */
    private fun daysSinceRevisionReminder(lastRevisionReminderDate: Long, now: Date): Long {
        if (lastRevisionReminderDate == 0L) return Long.MAX_VALUE
        val diff = now.toDateOnly().time - Date(lastRevisionReminderDate).toDateOnly().time
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    /** Liczba pełnych dni od ostatniego zaliczenia; [Long.MAX_VALUE] gdy nigdy nie zaliczono. */
    private fun daysSinceLastDone(lastStreakUpdateDate: Date?, now: Date): Long {
        if (lastStreakUpdateDate == null) return Long.MAX_VALUE
        val diff = now.toDateOnly().time - lastStreakUpdateDate.toDateOnly().time
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    companion object {
        const val DEFAULT_STREAK_MIN_VALUE = 2
        val DEFAULT_WINBACK_DAYS = listOf(7, 14)
        const val DEFAULT_MIN_WEAK_QUESTIONS = 3
        const val DEFAULT_REVISION_INTERVAL_DAYS = 3
    }
}

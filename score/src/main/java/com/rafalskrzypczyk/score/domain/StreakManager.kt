package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.core.utils.toDateOnly
import jakarta.inject.Inject
import java.util.Calendar
import java.util.Date

class StreakManager @Inject constructor(
    private val scoreManager: ScoreManager
) {
    companion object {
        private const val STREAK_POINTS_THRESHOLD = 3
    }

    private var increaseStreakPoints = 0

    fun increaseStreakByQuestions() {
        increaseStreakPoints++
        if (increaseStreakPoints >= STREAK_POINTS_THRESHOLD) {
            increaseStreakForToday()
        }
    }

    fun increaseStreak() {
        increaseStreakForToday()
    }

    fun validateStreak() {
        val score = scoreManager.getScore()
        val lastStreakDate = score.lastStreakUpdateDate?.toDateOnly()
        val streakPendingDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }.time.toDateOnly()

        if (lastStreakDate != null && lastStreakDate < streakPendingDate) {
            clearStreak()
        }
    }

    fun getStreakState(lastStreakUpdateDate: Date): StreakState {
        return when(lastStreakUpdateDate.toDateOnly()) {
            Calendar.getInstance().time.toDateOnly() -> StreakState.DONE
            Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time.toDateOnly() -> StreakState.PENDING
            else -> {
                clearStreak()
                StreakState.MISSED
            }
        }
    }

    private fun increaseStreakForToday() {
        val score = scoreManager.getScore()

        val today = Calendar.getInstance().time.toDateOnly()

        val lastUpdate = score.lastStreakUpdateDate?.toDateOnly()

        if (lastUpdate == null || lastUpdate < today) {
            scoreManager.updateScore(score.copy(
                streak = score.streak + 1,
                lastStreakUpdateDate = today
            ))
        }
    }

    private fun clearStreak() {
        val score = scoreManager.getScore()

        if(score.streak == 0) return

        scoreManager.updateScore(score.copy(
            streak = 0,
        ))
    }
}
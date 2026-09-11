package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.core.utils.TimeProvider
import com.rafalskrzypczyk.core.utils.toDateOnly
import jakarta.inject.Inject
import java.util.Calendar
import java.util.Date

class StreakManager @Inject constructor(
    private val scoreManager: ScoreManager,
    private val gameplayConfig: GameplayConfigProvider,
    private val timeProvider: TimeProvider,
    private val analyticsLogger: AnalyticsLogger,
) {
    private var increaseStreakPoints = 0

    private fun today(): Date = timeProvider.now().toDateOnly()

    private fun yesterday(): Date = Calendar.getInstance().apply {
        time = timeProvider.now()
        add(Calendar.DAY_OF_MONTH, -1)
    }.time.toDateOnly()

    fun increaseStreakByQuestions(): Boolean {
        increaseStreakPoints++
        if (increaseStreakPoints >= gameplayConfig.streakPointsThreshold()) {
            return increaseStreakForToday()
        }
        return false
    }

    fun increaseStreak(): Boolean {
        return increaseStreakForToday()
    }

    fun validateStreak() {
        val score = scoreManager.getScore()
        val lastStreakDate = score.lastStreakUpdateDate?.toDateOnly()
        val streakPendingDate = yesterday()

        if (lastStreakDate != null && lastStreakDate < streakPendingDate) {
            clearStreak()
        }
    }

    fun getStreakState(lastStreakUpdateDate: Date): StreakState {
        return when(lastStreakUpdateDate.toDateOnly()) {
            today() -> StreakState.DONE
            yesterday() -> StreakState.PENDING
            else -> {
                clearStreak()
                StreakState.MISSED
            }
        }
    }

    private fun increaseStreakForToday(): Boolean {
        val score = scoreManager.getScore()

        val today = today()

        val lastUpdate = score.lastStreakUpdateDate?.toDateOnly()

        if (lastUpdate == null || lastUpdate < today) {
            val newStreak = score.streak + 1
            scoreManager.updateScore(score.copy(
                streak = newStreak,
                lastStreakUpdateDate = today
            ))
            // Zdarzenie stoi tutaj, a nie w ViewModelach: updateStreak() wola kazdy z szesciu
            // trybow, a warunek powyzej jest jedyna trwala bramka „co najwyzej raz dziennie"
            // (lastStreakUpdateDate jest utrwalane razem z wynikiem).
            analyticsLogger.log(AnalyticsEvent.StreakIncremented(newStreak))
            return true
        }
        return false
    }

    private fun clearStreak() {
        val score = scoreManager.getScore()

        if(score.streak == 0) return

        scoreManager.updateScore(score.copy(
            streak = 0,
        ))
    }
}
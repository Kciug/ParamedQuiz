package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.core.utils.TimeProvider
import com.rafalskrzypczyk.score.domain.ScoreManager
import javax.inject.Inject

class UpdateLastDailyExerciseDateUC @Inject constructor(
    private val scoreManager: ScoreManager,
    private val timeProvider: TimeProvider
) {
    operator fun invoke() {
        val today = timeProvider.now()
        val score = scoreManager.getScore()

        scoreManager.updateScore(score.copy(
            lastDailyExerciseDate = today
        ))
    }
}
package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.ScoreManager
import java.util.Calendar
import javax.inject.Inject

class UpdateLastDailyExerciseDateUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke() {
        val today = Calendar.getInstance().time
        val score = scoreManager.getScore()

        scoreManager.updateScore(score.copy(
            lastDailyExerciseDate = today
        ))
    }
}
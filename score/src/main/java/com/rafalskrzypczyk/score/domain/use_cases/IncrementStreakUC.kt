package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.ScoreManager
import java.util.Calendar
import javax.inject.Inject

class IncrementStreakUC @Inject constructor(
    private val scoreManager: ScoreManager
){
    operator fun invoke() {
        val score = scoreManager.getScore()

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val lastUpdate = score.lastStreakUpdateDate?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        if (lastUpdate == null || lastUpdate < today) {
            scoreManager.updateScore(score.copy(
                streak = score.streak + 1,
                lastStreakUpdateDate = today
            ))
        }
    }
}
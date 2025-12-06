package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.ScoreManager
import javax.inject.Inject

class GetStreakUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke(): Int {
        return scoreManager.getScore().streak
    }
}
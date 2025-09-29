package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.StreakManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserScoreUC @Inject constructor(
    private val scoreManager: ScoreManager,
    private val streakManager: StreakManager
) {
    operator fun invoke() : Flow<Score> {
        streakManager.validateStreak()
        return scoreManager.getScoreFlow()
    }
}
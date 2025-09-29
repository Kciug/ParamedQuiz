package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserScoreUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke() : Flow<Score> = scoreManager.getScoreFlow()
}
package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.score.ScoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserScoreUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke() : Flow<Score> = scoreManager.getScoreFlow()
}
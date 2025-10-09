package com.rafalskrzypczyk.home_screen.domain.user_page

import com.rafalskrzypczyk.home_screen.domain.CalculateResultUC
import com.rafalskrzypczyk.score.domain.ScoreManager
import javax.inject.Inject

class GetOverallResultUC @Inject constructor(
    private val scoreManager: ScoreManager,
    private val calculateResult: CalculateResultUC
) {
    operator fun invoke(): Int? {
        val score = scoreManager.getScore()
        return calculateResult(score.seenQuestions)
    }
}
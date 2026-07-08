package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.ScoreManager
import javax.inject.Inject

class UpdateBestSwipeComboUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke(combo: Int) {
        val currentScore = scoreManager.getScore()
        if (combo > currentScore.bestSwipeCombo) {
            scoreManager.updateScore(currentScore.copy(bestSwipeCombo = combo))
        }
    }
}

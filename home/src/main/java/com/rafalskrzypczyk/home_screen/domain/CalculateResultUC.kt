package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.home_screen.domain.models.ModeResult
import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import javax.inject.Inject

class CalculateResultUC @Inject constructor() {
    operator fun invoke(scoreAnnotations: List<QuestionAnnotation>) : ModeResult? {
        if(scoreAnnotations.isEmpty()) return null

        val correctAnswers = scoreAnnotations.sumOf { it.timesCorrect }.toInt()
        val totalAnswers = scoreAnnotations.sumOf { it.timesSeen }.toInt()

        val score = if (totalAnswers > 0) {
            ((correctAnswers / totalAnswers.toFloat()) * 100).toInt()
        } else {
            0
        }

        return ModeResult(
            score = score,
            correctAnswers = correctAnswers,
            totalAnswers = totalAnswers
        )
    }
}

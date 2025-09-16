package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import javax.inject.Inject

class CalculateResultUC @Inject constructor() {
    operator fun invoke(scoreAnnotations: List<QuestionAnnotation>) : Int {
        val correctAnswers = scoreAnnotations.sumOf { it.timesCorrect }
        val totalAnswers = scoreAnnotations.sumOf { it.timesSeen }

        return if (totalAnswers > 0) {
            ((correctAnswers / totalAnswers.toFloat()) * 100).toInt()
        } else {
            0
        }
    }
}
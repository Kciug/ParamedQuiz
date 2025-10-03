package com.rafalskrzypczyk.home_screen.domain.user_page

import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import com.rafalskrzypczyk.home_screen.domain.models.SimpleQuestion
import com.rafalskrzypczyk.home_screen.domain.models.combineQuestionsWithStats
import com.rafalskrzypczyk.score.domain.ScoreManager
import javax.inject.Inject

class GetBestQuestionsUC @Inject constructor(
    private val scoreManager: ScoreManager,
) {
    operator fun invoke(questions: List<SimpleQuestion>): List<QuestionWithStats> {
        val seenQuestions = scoreManager.getScore().seenQuestions

        return combineQuestionsWithStats(questions, seenQuestions)
            .sortedByDescending { it.correctAnswers - it.wrongAnswers }.take(5)
    }
}
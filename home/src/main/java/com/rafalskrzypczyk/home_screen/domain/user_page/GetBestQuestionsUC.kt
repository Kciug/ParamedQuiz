package com.rafalskrzypczyk.home_screen.domain.user_page

import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import javax.inject.Inject

class GetBestQuestionsUC @Inject constructor() {
    operator fun invoke(combinedQuestions: List<QuestionWithStats>, questionsToTake: Int): List<QuestionWithStats> {
        return combinedQuestions.sortedByDescending { it.correctAnswers - it.wrongAnswers }.take(questionsToTake)
    }
}
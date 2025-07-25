package com.rafalskrzypczyk.main_mode.domain.quiz

import com.rafalskrzypczyk.score.domain.UpdateSeenQuestionUC
import javax.inject.Inject

data class MMQuizUseCases @Inject constructor(
    val getQuestionsForCategory: GetShuffledQuestionsForCategoryUC,
    val getUpdatedQuestions: GetUpdatedQuestionsUC,
    val updateScore: UpdateSeenQuestionUC
)

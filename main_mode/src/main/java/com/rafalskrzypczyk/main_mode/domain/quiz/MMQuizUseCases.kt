package com.rafalskrzypczyk.main_mode.domain.quiz

import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import com.rafalskrzypczyk.score.domain.use_cases.IncrementStreakUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class MMQuizUseCases @Inject constructor(
    val getQuestionsForCategory: GetShuffledQuestionsForCategoryUC,
    val getUpdatedQuestions: GetUpdatedQuestionsUC,
    val evaluateAnswers: EvaluateAnswerUC,
    val getUserScore: GetUserScoreUC,
    val updateScore: UpdateScoreWithQuestionUC,
    val updateStreak: IncrementStreakUC
)

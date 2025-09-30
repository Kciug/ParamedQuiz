package com.rafalskrzypczyk.main_mode.domain.quiz_base

import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import jakarta.inject.Inject

data class BaseQuizUseCases @Inject constructor(
    val evaluateAnswers: EvaluateAnswerUC,
    val getUserScore: GetUserScoreUC,
    val updateScore: UpdateScoreWithQuestionUC
)

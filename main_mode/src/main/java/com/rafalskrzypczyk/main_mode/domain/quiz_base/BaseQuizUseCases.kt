package com.rafalskrzypczyk.main_mode.domain.quiz_base

import com.rafalskrzypczyk.core.domain.use_cases.IncrementCompletedQuizzesUC
import com.rafalskrzypczyk.firestore.domain.use_cases.ReportIssueUC
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class BaseQuizUseCases @Inject constructor(
    val updateScore: UpdateScoreWithQuestionUC,
    val getUserScore: GetUserScoreUC,
    val evaluateAnswers: EvaluateAnswerUC,
    val getStreak: GetStreakUC,
    val reportIssue: ReportIssueUC,
    val incrementCompletedQuizzes: IncrementCompletedQuizzesUC
)
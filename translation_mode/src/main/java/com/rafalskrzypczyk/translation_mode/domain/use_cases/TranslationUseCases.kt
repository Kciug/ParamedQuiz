package com.rafalskrzypczyk.translation_mode.domain.use_cases

import com.rafalskrzypczyk.core.domain.use_cases.IncrementCompletedQuizzesUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakByQuestionsUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class TranslationUseCases @Inject constructor(
    val getTranslationQuestions: GetTranslationQuestionsUseCase,
    val getUpdatedTranslationQuestions: GetUpdatedTranslationQuestionsUseCase,
    val sendTranslationReport: SendTranslationReportUseCase,
    val updateScoreWithQuestion: UpdateScoreWithQuestionUC,
    val increaseStreakByQuestions: IncreaseStreakByQuestionsUC,
    val getUserScore: GetUserScoreUC,
    val incrementCompletedQuizzes: IncrementCompletedQuizzesUC
)

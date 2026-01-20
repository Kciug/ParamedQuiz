package com.rafalskrzypczyk.translation_mode.domain.use_cases

import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakByQuestionsUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class TranslationUseCases @Inject constructor(
    val getTranslationQuestions: GetTranslationQuestionsUseCase,
    val getUpdatedTranslationQuestions: GetUpdatedTranslationQuestionsUseCase,
    val sendTranslationReport: SendTranslationReportUseCase,
    val updateScoreWithQuestion: UpdateScoreWithQuestionUC,
    val increaseStreakByQuestions: IncreaseStreakByQuestionsUC
)
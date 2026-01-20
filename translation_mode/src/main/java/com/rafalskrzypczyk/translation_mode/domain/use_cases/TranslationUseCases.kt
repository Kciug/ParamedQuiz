package com.rafalskrzypczyk.translation_mode.domain.use_cases

import javax.inject.Inject

data class TranslationUseCases @Inject constructor(
    val getTranslationQuestions: GetTranslationQuestionsUseCase,
    val sendTranslationReport: SendTranslationReportUseCase
)
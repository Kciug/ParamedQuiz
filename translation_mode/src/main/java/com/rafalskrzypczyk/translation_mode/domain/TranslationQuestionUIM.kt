package com.rafalskrzypczyk.translation_mode.domain

data class TranslationQuestionUIM(
    val id: Long,
    val phrase: String,
    val possibleTranslations: List<String>,
    val userAnswer: String = "",
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false
)
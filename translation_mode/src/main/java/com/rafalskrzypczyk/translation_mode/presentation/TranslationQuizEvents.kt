package com.rafalskrzypczyk.translation_mode.presentation

sealed class TranslationQuizEvents {
    data class OnAnswerChanged(val text: String) : TranslationQuizEvents()
    object OnSubmitAnswer : TranslationQuizEvents()
    object OnNextQuestion : TranslationQuizEvents()
    object OnBackPressed : TranslationQuizEvents()
    object OnBackDiscarded : TranslationQuizEvents()
    data class OnBackConfirmed(val navigateBack: () -> Unit) : TranslationQuizEvents()
    data class ToggleReportDialog(val show: Boolean) : TranslationQuizEvents()
    data class OnReportIssueDescriptionChanged(val description: String) : TranslationQuizEvents()
    object OnReportIssue : TranslationQuizEvents()
    data class ToggleReviewDialog(val show: Boolean) : TranslationQuizEvents()
}

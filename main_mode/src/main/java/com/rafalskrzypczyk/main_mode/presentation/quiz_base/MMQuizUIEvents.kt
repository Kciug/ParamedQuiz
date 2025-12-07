package com.rafalskrzypczyk.main_mode.presentation.quiz_base

sealed class MMQuizUIEvents {
    object OnSubmitAnswer : MMQuizUIEvents()
    object OnNextQuestion : MMQuizUIEvents()
    object OnBackPressed : MMQuizUIEvents()
    object OnBackDiscarded : MMQuizUIEvents()
    data class OnBackConfirmed(val navigateBack: () -> Unit) : MMQuizUIEvents()
    data class OnAnswerClicked(val answerId: Long) : MMQuizUIEvents()
    data class ToggleReviewDialog(val show: Boolean) : MMQuizUIEvents()
    data class ToggleReportDialog(val show: Boolean) : MMQuizUIEvents()
    data class OnReportIssue(val description: String) : MMQuizUIEvents()
}
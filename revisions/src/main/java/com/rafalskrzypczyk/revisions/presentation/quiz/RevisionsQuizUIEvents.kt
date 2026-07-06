package com.rafalskrzypczyk.revisions.presentation.quiz

sealed interface RevisionsQuizUIEvents {
    data class OnAnswerSelected(val answerId: Long) : RevisionsQuizUIEvents
    object OnSubmitAnswer : RevisionsQuizUIEvents
    object OnNextQuestion : RevisionsQuizUIEvents
    data class OnTranslationAnswerChanged(val text: String) : RevisionsQuizUIEvents
    object OnBackPressed : RevisionsQuizUIEvents
    object OnBackDiscarded : RevisionsQuizUIEvents
    object OnBackConfirmed : RevisionsQuizUIEvents
    data class ToggleReviewDialog(val show: Boolean) : RevisionsQuizUIEvents
    data class ToggleReportDialog(val show: Boolean) : RevisionsQuizUIEvents
    data class OnReportIssueDescriptionChanged(val description: String) : RevisionsQuizUIEvents
    object OnReportIssue : RevisionsQuizUIEvents
    object OnAdShown : RevisionsQuizUIEvents
    object OnAdDismissed : RevisionsQuizUIEvents
}

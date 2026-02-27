package com.rafalskrzypczyk.swipe_mode.presentation

sealed interface SwipeModeUIEvents {
    data class SubmitAnswer(val questionId: Long, val isCorrect: Boolean) : SwipeModeUIEvents
    object OnBackPressed: SwipeModeUIEvents
    object OnBackDiscarded: SwipeModeUIEvents
    data class OnBackConfirmed(val navigateBack: () -> Unit): SwipeModeUIEvents
    data class ToggleReportDialog(val show: Boolean) : SwipeModeUIEvents
    data class OnReportIssue(val description: String) : SwipeModeUIEvents
    object OnAdDismissed : SwipeModeUIEvents
    object OnAdShown : SwipeModeUIEvents

    object BuyMode : SwipeModeUIEvents
    data class ExitTrial(val navigateBack: () -> Unit) : SwipeModeUIEvents
}
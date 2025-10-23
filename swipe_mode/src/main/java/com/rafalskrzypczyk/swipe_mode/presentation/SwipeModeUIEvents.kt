package com.rafalskrzypczyk.swipe_mode.presentation

sealed interface SwipeModeUIEvents {
    data class SubmitAnswer(val questionId: Long, val isCorrect: Boolean) : SwipeModeUIEvents
    object OnBackPressed: SwipeModeUIEvents
    object OnBackDiscarded: SwipeModeUIEvents
    data class OnBackConfirmed(val navigateBack: () -> Unit): SwipeModeUIEvents
}
package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

sealed interface MMQuizUIEvents {
    object OnBackPressed: MMQuizUIEvents
    object OnBackDiscarded: MMQuizUIEvents
    data class OnAnswerClicked(val answerId: Long) : MMQuizUIEvents
    object OnSubmitAnswer : MMQuizUIEvents
    object OnNextQuestion : MMQuizUIEvents
}
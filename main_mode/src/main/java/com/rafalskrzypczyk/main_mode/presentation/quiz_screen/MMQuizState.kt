package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import com.rafalskrzypczyk.core.api_response.ResponseState

data class MMQuizState (
    val responseState: ResponseState = ResponseState.Idle,
    val showExitConfirmation: Boolean = false,
    val categoryTitle: String = "",
    val currentQuestionNumber: Int = 0,
    val questionsCount: Int = 0,
    val questionText: String = "",
    val answers: List<AnswerUIM> = emptyList(),
    val isAnswerSubmitted: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val correctAnswers: List<String> = emptyList(),
    val isQuizFinished: Boolean = false,
)
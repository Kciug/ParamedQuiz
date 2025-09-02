package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import com.rafalskrzypczyk.core.api_response.ResponseState

data class MMQuizState (
    val userScore: Int = 0,
    val correctAnswers: Int = 0,
    val responseState: ResponseState = ResponseState.Idle,
    val showExitConfirmation: Boolean = false,
    val categoryTitle: String = "",
    val currentQuestionNumber: Int = 0,
    val questionsCount: Int = 0,
    val question: QuestionUIM = QuestionUIM(),
    val isQuizFinished: Boolean = false,
)

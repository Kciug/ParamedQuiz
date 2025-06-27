package com.rafalskrzypczyk.swipe_mode.presentation

import com.rafalskrzypczyk.core.api_response.ResponseState

data class SwipeModeState(
    val responseState: ResponseState = ResponseState.Idle,
    val currentQuestionNumber: Int = 0,
    val questionsCount: Int = 0,
    val questionsPair: List<SwipeQuestionUIModel> = emptyList(),
    val answerResult: SwipeQuizResult = SwipeQuizResult.NONE,
    val isQuizFinished: Boolean = false,
    val correctAnswers: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
)

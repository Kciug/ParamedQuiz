package com.rafalskrzypczyk.swipe_mode.presentation

data class SwipeModeAnswerResult(
    val questionId: Long = 0,
    val result: SwipeQuizResult = SwipeQuizResult.NONE
)

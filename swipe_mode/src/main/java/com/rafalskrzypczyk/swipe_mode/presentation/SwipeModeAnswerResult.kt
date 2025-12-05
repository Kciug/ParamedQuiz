package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class SwipeModeAnswerResult(
    val questionId: Long = 0,
    val result: SwipeQuizResult = SwipeQuizResult.NONE
)

package com.rafalskrzypczyk.core.composables.quiz_finished

import androidx.compose.runtime.Immutable

@Immutable
data class QuizFinishedState(
    val seenQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val streak: Int? = null,
    val points: Int = 0,
    val earnedPoints: Int = 0
)

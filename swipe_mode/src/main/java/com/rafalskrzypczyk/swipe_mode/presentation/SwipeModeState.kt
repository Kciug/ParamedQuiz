package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState

@Immutable
data class SwipeModeState(
    val responseState: ResponseState = ResponseState.Idle,
    val userScore: Int = 0,
    val showExitConfirmation: Boolean = false,
    val currentQuestionNumber: Int = 0,
    val questionsCount: Int = 0,
    val questionsPair: List<SwipeQuestionUIModel> = emptyList(),
    val answerResult: SwipeModeAnswerResult = SwipeModeAnswerResult(),
    val isQuizFinished: Boolean = false,
    val quizFinishedState: QuizFinishedState = QuizFinishedState(),
    val correctAnswers: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    
    // New Metrics
    val averageResponseTime: Long = 0L,
    val totalQuizDuration: Long = 0L,
    val type1Errors: Int = 0, // False Negatives (User said False, was True)
    val type2Errors: Int = 0  // False Positives (User said True, was False)
)

package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState

@Immutable
data class QuizState (
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val correctAnswers: Int = 0,
    val responseState: ResponseState = ResponseState.Idle,
    val showExitConfirmation: Boolean = false,
    val categoryTitle: String = "",
    val currentQuestionNumber: Int = 0,
    val questionsCount: Int = 0,
    val question: QuestionUIM = QuestionUIM(),
    val isQuizFinished: Boolean = false,
    val quizFinishedState: QuizFinishedState = QuizFinishedState(),
    
    // Stats & History
    val quizStartTime: Long = 0L,
    val totalResponseTime: Long = 0L,
    val answeredQuestions: List<QuestionUIM> = emptyList(),
    val averagePrecision: Int = 0,
    
    // Dialogs
    val showReviewDialog: Boolean = false,
    val showReportDialog: Boolean = false,
    val showReportSuccessToast: Boolean = false,
    
    // Mode specific
    val isDailyExercise: Boolean = false
)
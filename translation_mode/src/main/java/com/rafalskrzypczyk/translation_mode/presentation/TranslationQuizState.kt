package com.rafalskrzypczyk.translation_mode.presentation

import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM

data class TranslationQuizState(
    val responseState: ResponseState = ResponseState.Idle,
    val questions: List<TranslationQuestionUIM> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userScore: Int = 0,
    val correctAnswersCount: Int = 0,
    val isQuizFinished: Boolean = false,
    val quizFinishedState: QuizFinishedState = QuizFinishedState(),
    val showExitConfirmation: Boolean = false,
    val showReportDialog: Boolean = false,
    val showReportSuccessToast: Boolean = false
) {
    val currentQuestion: TranslationQuestionUIM?
        get() = questions.getOrNull(currentQuestionIndex)
}
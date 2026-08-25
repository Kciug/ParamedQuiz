package com.rafalskrzypczyk.revisions.presentation.quiz

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.QuestionUIM
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion

@Immutable
data class RevisionsQuizState(
    val mode: QuizMode = QuizMode.MainMode,
    val responseState: ResponseState = ResponseState.Idle,
    val mcQuestions: List<QuestionUIM> = emptyList(),
    val currentMcQuestionIndex: Int = 0,
    val translationQuestions: List<TranslationQuestionUIM> = emptyList(),
    val currentTranslationQuestionIndex: Int = 0,
    val quizFinished: Boolean = false,
    val quizFinishedState: QuizFinishedState = QuizFinishedState(),
    val showExitConfirmation: Boolean = false,
    val originalQuestions: List<RevisionQuestion> = emptyList(),
    val failedQuestionIds: Set<Long> = emptySet(),
    val attemptedQuestionIds: Set<Long> = emptySet(),
    val remainingQueueIds: Set<Long> = emptySet(),
    val showReviewDialog: Boolean = false,
    val currentQuestionNumber: Int = 1,
    val totalQuestions: Int = 0,
    val correctAnswersCount: Int = 0,
    /**
     * Ile pytan zaliczono za pierwszym podejsciem, bez korekt. Wypelniane dopiero w finishQuiz()
     * razem z [attemptedQuestionIds] - w trakcie sesji obie wartosci nie sa miarodajne.
     */
    val firstAttemptCorrectCount: Int = 0,
    val userScore: Int = 0,
    val isCorrection: Boolean = false,
    val showReportDialog: Boolean = false,
    val reportIssueDescription: String = "",
    val progress: Int = 1,
    val range: Int = 1,
    val errorCounts: Map<Long, Int> = emptyMap(),
    val showAd: Boolean = false
) {
    /**
     * Skutecznosc pierwszego podejscia w procentach. Liczona po pytaniach faktycznie podjetych,
     * wiec wczesniejsze wyjscie z sesji nie zaniza wyniku o pytania, ktorych uzytkownik nie widzial.
     */
    val firstAttemptAccuracy: Int
        get() = if (attemptedQuestionIds.isEmpty()) {
            0
        } else {
            ((firstAttemptCorrectCount.toFloat() / attemptedQuestionIds.size) * 100).toInt()
        }

    val currentQuestionUIM: QuestionUIM?
        get() = mcQuestions.getOrNull(currentMcQuestionIndex)

    val currentTranslationQuestionUIM: TranslationQuestionUIM?
        get() = translationQuestions.getOrNull(currentTranslationQuestionIndex)
}

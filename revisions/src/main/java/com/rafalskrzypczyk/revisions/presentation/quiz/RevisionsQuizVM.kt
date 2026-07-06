package com.rafalskrzypczyk.revisions.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.report_issues.IssueReport
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.firestore.domain.use_cases.ReportIssueUC
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.submitAnswer
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.toUIM
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.updateAnswers
import com.rafalskrzypczyk.revisions.domain.engine.RevisionsSessionEngine
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.revisions.domain.use_cases.GetRevisionsQuestionsUC
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.StreakManager
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevisionsQuizVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRevisionsQuestions: GetRevisionsQuestionsUC,
    private val updateScoreWithQuestion: UpdateScoreWithQuestionUC,
    private val scoreManager: ScoreManager,
    private val streakManager: StreakManager,
    private val reportIssueUC: ReportIssueUC,
    private val adHandler: QuizAdHandler
) : ViewModel() {

    private val mode: QuizMode = QuizMode.valueOf(savedStateHandle.get<String>("mode") ?: QuizMode.MainMode.name)
    private val categoryId: Long? = savedStateHandle.get<Long>("categoryId")
    private val criterion: RevisionCriterion = RevisionCriterion.valueOf(savedStateHandle.get<String>("criterion") ?: RevisionCriterion.WORST.name)
    private val limit: Int? = savedStateHandle.get<Int>("limit")

    private val _state = MutableStateFlow(RevisionsQuizState(mode = mode))
    val state = _state.asStateFlow()

    private val engine = RevisionsSessionEngine()

    private var currentQuestionStartTime = 0L
    private var isStreakUpdatedInSession = false

    init {
        collectScore()
        loadQuestions()
        adHandler.initialize(viewModelScope)
    }

    private fun collectScore() {
        viewModelScope.launch {
            scoreManager.getScoreFlow().collectLatest { score ->
                _state.update { it.copy(userScore = score.score) }
            }
        }
    }

    private fun loadQuestions() {
        _state.update { it.copy(responseState = ResponseState.Loading) }
        viewModelScope.launch {
            getRevisionsQuestions(mode, categoryId, criterion, limit).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        val questions = response.data
                        engine.startSession(questions)
                        _state.update {
                            it.copy(
                                responseState = ResponseState.Success,
                                originalQuestions = questions,
                                totalQuestions = engine.getInitialSize()
                            )
                        }
                        displayCurrentQuestion()
                    }
                    is Response.Error -> {
                        _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                    }
                    Response.Loading -> {
                        _state.update { it.copy(responseState = ResponseState.Loading) }
                    }
                }
            }
        }
    }

    fun onEvent(event: RevisionsQuizUIEvents) {
        when (event) {
            is RevisionsQuizUIEvents.OnAnswerSelected -> onAnswerSelected(event.answerId)
            RevisionsQuizUIEvents.OnSubmitAnswer -> submitAnswer()
            RevisionsQuizUIEvents.OnNextQuestion -> displayNextQuestion()
            is RevisionsQuizUIEvents.OnTranslationAnswerChanged -> onTranslationAnswerChanged(event.text)
            RevisionsQuizUIEvents.OnBackPressed -> _state.update { it.copy(showExitConfirmation = true) }
            RevisionsQuizUIEvents.OnBackDiscarded -> _state.update { it.copy(showExitConfirmation = false) }
            RevisionsQuizUIEvents.OnBackConfirmed -> handleExitQuiz()
            is RevisionsQuizUIEvents.ToggleReviewDialog -> _state.update { it.copy(showReviewDialog = event.show) }
            is RevisionsQuizUIEvents.ToggleReportDialog -> _state.update { it.copy(showReportDialog = event.show) }
            is RevisionsQuizUIEvents.OnReportIssueDescriptionChanged -> _state.update { it.copy(reportIssueDescription = event.description) }
            RevisionsQuizUIEvents.OnReportIssue -> reportIssue()
            RevisionsQuizUIEvents.OnAdShown -> {}
            RevisionsQuizUIEvents.OnAdDismissed -> handleAdDismissed()
        }
    }

    private fun displayCurrentQuestion() {
        val currentQuestion = engine.getCurrentQuestion()
        if (currentQuestion == null) {
            finishQuiz()
            return
        }

        currentQuestionStartTime = System.currentTimeMillis()

        val progressNum = (engine.getAttemptedQuestionIds().size + 1).coerceAtMost(engine.getInitialSize())
        val isCorrection = engine.getAttemptedQuestionIds().contains(currentQuestion.id)

        val progressValue: Int
        val rangeValue: Int
        if (isCorrection) {
            val totalCorrectionCount = engine.getInitialSize() - engine.getFirstInteractionCorrectCount()
            rangeValue = totalCorrectionCount.coerceAtLeast(1)
            progressValue = (totalCorrectionCount - engine.getCurrentQueueSize() + 1).coerceIn(1, rangeValue)
        } else {
            rangeValue = engine.getInitialSize()
            progressValue = progressNum
        }

        when (currentQuestion) {
            is RevisionQuestion.Main -> {
                val uim = currentQuestion.question.toUIM()
                _state.update {
                    it.copy(
                        mcQuestions = listOf(uim),
                        currentMcQuestionIndex = 0,
                        currentQuestionNumber = progressNum,
                        correctAnswersCount = engine.getCorrectAnswersCount(),
                        isCorrection = isCorrection,
                        progress = progressValue,
                        range = rangeValue
                    )
                }
            }
            is RevisionQuestion.Cem -> {
                val uim = currentQuestion.question.toUIM()
                _state.update {
                    it.copy(
                        mcQuestions = listOf(uim),
                        currentMcQuestionIndex = 0,
                        currentQuestionNumber = progressNum,
                        correctAnswersCount = engine.getCorrectAnswersCount(),
                        isCorrection = isCorrection,
                        progress = progressValue,
                        range = rangeValue
                    )
                }
            }
            is RevisionQuestion.Translation -> {
                val uim = currentQuestion.question.toUIM()
                _state.update {
                    it.copy(
                        translationQuestions = listOf(uim),
                        currentTranslationQuestionIndex = 0,
                        currentQuestionNumber = progressNum,
                        correctAnswersCount = engine.getCorrectAnswersCount(),
                        isCorrection = isCorrection,
                        progress = progressValue,
                        range = rangeValue
                    )
                }
            }
        }
    }

    private fun onAnswerSelected(answerId: Long) {
        val currentQ = _state.value.currentQuestionUIM ?: return
        val updatedAnswers = currentQ.answers.map { answer ->
            if (answer.id == answerId) answer.copy(isSelected = !answer.isSelected)
            else answer
        }
        _state.update {
            it.copy(
                mcQuestions = listOf(currentQ.updateAnswers(updatedAnswers))
            )
        }
    }

    private fun onTranslationAnswerChanged(text: String) {
        val currentQ = _state.value.currentTranslationQuestionUIM ?: return
        _state.update {
            it.copy(
                translationQuestions = listOf(currentQ.copy(userAnswer = text))
            )
        }
    }

    private fun submitAnswer() {
        when (mode) {
            QuizMode.TranslationMode -> submitTranslationAnswer()
            else -> submitMultipleChoiceAnswer()
        }
    }

    private fun submitMultipleChoiceAnswer() {
        val currentQ = _state.value.currentQuestionUIM ?: return
        val selectedAnswers = currentQ.answers.filter { it.isSelected }
        val selectedIds = selectedAnswers.map { it.id }
        val correctIds = currentQ.correctAnswerIds

        val correctlySelectedCount = selectedIds.count { it in correctIds }
        val unionSize = selectedIds.size + correctIds.size - correctlySelectedCount

        val precision = if (unionSize > 0) {
            ((correctlySelectedCount.toFloat() / unionSize) * 100).toInt()
        } else {
            0
        }
        val isCorrect = precision == 100

        val result = engine.submitAnswer(isCorrect) ?: return

        if (result.isFirstAttempt) {
            val earned = updateScoreWithQuestion(currentQ.id, isCorrect)
            engine.addEarnedPoints(earned)
            if (isCorrect && !isStreakUpdatedInSession) {
                viewModelScope.launch {
                    streakManager.increaseStreak()
                    isStreakUpdatedInSession = true
                }
            }
        }

        val processedQ = currentQ.submitAnswer(isCorrect, precision)
        _state.update {
            it.copy(
                mcQuestions = listOf(processedQ)
            )
        }
    }

    private fun submitTranslationAnswer() {
        val currentQ = _state.value.currentTranslationQuestionUIM ?: return
        val userAnswerTrimmed = currentQ.userAnswer.trim().lowercase()
        val isCorrect = currentQ.possibleTranslations.any { it.trim().lowercase() == userAnswerTrimmed }

        val result = engine.submitAnswer(isCorrect) ?: return

        if (result.isFirstAttempt) {
            val earned = updateScoreWithQuestion(currentQ.id, isCorrect)
            engine.addEarnedPoints(earned)
            if (isCorrect && !isStreakUpdatedInSession) {
                viewModelScope.launch {
                    streakManager.increaseStreak()
                    isStreakUpdatedInSession = true
                }
            }
        }

        val processedQ = currentQ.copy(
            isAnswered = true,
            isCorrect = isCorrect
        )
        _state.update {
            it.copy(
                translationQuestions = listOf(processedQ)
            )
        }
    }

    private fun displayNextQuestion() {
        val nextQuestion = engine.getCurrentQuestion()
        val isFinished = nextQuestion == null
        val answeredCount = engine.getAttemptedQuestionIds().size

        if (adHandler.shouldShowAd(
                answeredCount = answeredCount,
                isQuizFinished = isFinished,
                ignoreThreshold = false
            )
        ) {
            _state.update { it.copy(showAd = true) }
        } else {
            proceedToNext()
        }
    }

    private fun proceedToNext() {
        val currentQuestion = engine.getCurrentQuestion()
        if (currentQuestion == null) {
            finishQuiz()
        } else {
            displayCurrentQuestion()
        }
    }

    private fun handleExitQuiz() {
        _state.update { it.copy(showExitConfirmation = false) }
        val answeredCount = engine.getAttemptedQuestionIds().size
        if (adHandler.shouldShowAd(
                answeredCount = answeredCount,
                isQuizFinished = true,
                ignoreThreshold = false
            )
        ) {
            _state.update { it.copy(showAd = true) }
        } else {
            finishQuiz()
        }
    }

    private fun handleAdDismissed() {
        _state.update { it.copy(showAd = false) }
        adHandler.handleAdDismissed(
            onContinue = { proceedToNext() },
            onFinish = { finishQuiz() }
        )
    }

    private fun finishQuiz() {
        _state.update {
            it.copy(
                quizFinished = true,
                failedQuestionIds = engine.getFailedQuestionIds(),
                attemptedQuestionIds = engine.getAttemptedQuestionIds(),
                remainingQueueIds = engine.getQueueIds(),
                errorCounts = engine.getErrorCounts(),
                quizFinishedState = QuizFinishedState(
                    seenQuestions = engine.getPlayedQuestions().size,
                    correctAnswers = engine.getCorrectAnswersCount(),
                    points = scoreManager.getScore().score,
                    earnedPoints = engine.getTotalPointsEarned(),
                    isStreakUpdated = isStreakUpdatedInSession,
                    streak = scoreManager.getScore().streak
                )
            )
        }
    }

    private fun reportIssue() {
        val currentQuestion = engine.getCurrentQuestion() ?: return
        val questionId = currentQuestion.id
        val questionText = currentQuestion.text
        val description = _state.value.reportIssueDescription
        val report = IssueReport(
            questionId = questionId,
            questionContent = questionText,
            description = description,
            gameMode = "Revisions Mode"
        )
        viewModelScope.launch {
            reportIssueUC(report).collectLatest { response ->
                if (response is Response.Success) {
                    _state.update {
                        it.copy(
                            showReportDialog = false,
                            reportIssueDescription = ""
                        )
                    }
                }
            }
        }
    }

    private fun TranslationQuestionDTO.toUIM(): TranslationQuestionUIM {
        return TranslationQuestionUIM(
            id = this.id,
            phrase = this.phrase.trim(),
            possibleTranslations = this.translations.map { it.trim() }.filter { it.isNotBlank() }
        )
    }
}

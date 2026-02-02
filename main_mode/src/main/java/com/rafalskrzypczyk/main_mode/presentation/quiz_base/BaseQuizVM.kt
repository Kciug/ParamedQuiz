package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.report_issues.IssueReport
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.main_mode.domain.quiz_base.QuizEngine
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseQuizVM (
    private val useCases: BaseQuizUseCases,
    private val premiumStatusProvider: PremiumStatusProvider
): ViewModel() {
    @Suppress("PropertyName")
    protected val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    private val quizEngine = QuizEngine(useCases)

    protected var earnedPoints: Int = 0
    protected var isStreakUpdatedInSession: Boolean = false
    private var isAdsFree: Boolean = false
    
    // Timing
    private var currentQuestionStartTime: Long = 0L

    init {
        loadUserScore()
        viewModelScope.launch {
            premiumStatusProvider.isAdsFree.collectLatest { 
                isAdsFree = it
            }
        }
    }

    abstract suspend fun loadQuestions()

    fun onEvent(event: MMQuizUIEvents) {
        when (event) {
            MMQuizUIEvents.OnBackPressed -> _state.update { it.copy(showExitConfirmation = true) }
            MMQuizUIEvents.OnBackDiscarded -> _state.update { it.copy(showExitConfirmation = false) }
            is MMQuizUIEvents.OnAnswerClicked -> onAnswerClicked(event.answerId)
            MMQuizUIEvents.OnSubmitAnswer -> submitAnswer()
            MMQuizUIEvents.OnNextQuestion -> displayNextQuestion()
            is MMQuizUIEvents.OnBackConfirmed -> handleExitQuiz(event.navigateBack)
            is MMQuizUIEvents.ToggleReviewDialog -> toggleReviewDialog(event.show)
            is MMQuizUIEvents.ToggleReportDialog -> toggleReportDialog(event.show)
            is MMQuizUIEvents.OnReportIssue -> reportIssue(event.description)
            MMQuizUIEvents.OnAdDismissed -> finishQuiz()
            MMQuizUIEvents.OnAdShown -> onAdShown()
        }
    }

    private fun onAdShown() {
        // Do nothing, wait for dismissal
    }
    
    fun toggleReviewDialog(show: Boolean) {
        _state.update { it.copy(showReviewDialog = show) }
    }

    fun toggleReportDialog(show: Boolean) {
        _state.update { it.copy(showReportDialog = show) }
    }
    
    private fun reportIssue(description: String) {
        val currentQ = state.value.question
        val report = IssueReport(
            questionId = currentQ.id.toString(),
            questionContent = currentQ.questionText,
            description = description,
            gameMode = "Main Mode"
        )
        viewModelScope.launch {
            useCases.reportIssue(report).collectLatest { response ->
                if(response is Response.Success) {
                    _state.update { it.copy(showReportDialog = false, showReportSuccessToast = true) }
                }
            }
        }
    }

    protected fun loadUserScore() {
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { score ->
                _state.update { it.copy(userScore = score.score, userStreak = score.streak) }
            }
        }
    }

    private fun onAnswerClicked(answerId: Long) {
        val updatedAnswers = state.value.question.answers.map { answer ->
            if (answer.id == answerId) answer.copy(isSelected = !answer.isSelected)
            else answer
        }
        _state.update { it.copy(question = it.question.updateAnswers(updatedAnswers)) }
    }

    protected open fun submitAnswer() {
        val now = System.currentTimeMillis()
        val duration = now - currentQuestionStartTime
        
        val currentQ = state.value.question
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

        val isCorrect = quizEngine.submitAnswer(selectedIds)

        val processedQuestion = currentQ.submitAnswer(isCorrect, precision)
        val newHistory = state.value.answeredQuestions + processedQuestion
        
        val totalPrecision = newHistory.sumOf { it.userPrecision }
        val avgPrecision = if (newHistory.isNotEmpty()) totalPrecision / newHistory.size else 0

        _state.update {
            it.copy(
                question = processedQuestion,
                correctAnswers = quizEngine.getCorrectAnswers(),
                totalResponseTime = it.totalResponseTime + duration,
                answeredQuestions = newHistory,
                averagePrecision = avgPrecision
            )
        }

        val domainQ = quizEngine.getCurrentQuestion()
        if (domainQ != null) {
            earnedPoints += useCases.updateScore(domainQ.id, isCorrect)
        }
    }

    protected fun initializeQuiz(questions: List<Question>, title: String) {
        quizEngine.setQuestions(questions)
        _state.update {
            it.copy(
                responseState = ResponseState.Success,
                categoryTitle = title,
                questionsCount = quizEngine.getQuestionsCount(),
                quizStartTime = System.currentTimeMillis()
            )
        }
        displayQuestion()
    }

    protected fun updateQuizData(questions: List<Question>) {
        val oldQuestions = quizEngine.getAllQuestions()
        val updatedQuestions = mutableListOf<Question>()

        oldQuestions.forEach { question ->
            val updatedQuestion = questions.firstOrNull { it.id == question.id }
            if(updatedQuestion != null) {
                updatedQuestions.add(updatedQuestion)
            } else {
                updatedQuestions.add(question)
            }
        }
        quizEngine.setQuestions(updatedQuestions)
    }


    private fun displayQuestion() {
        val q = quizEngine.getCurrentQuestion()
        if (q == null) {
            _state.update { it.copy(isQuizFinished = true) }
            return
        }
        
        currentQuestionStartTime = System.currentTimeMillis()
        
        _state.update {
            it.copy(
                currentQuestionNumber = quizEngine.getCurrentQuestionNumber(),
                question = q.toUIM()
            )
        }
    }

    protected open fun displayNextQuestion() {
        val next = quizEngine.nextQuestion()
        if (next == null) {
            setFinishedState()
        } else {
            _state.update {
                it.copy(
                    currentQuestionNumber = quizEngine.getCurrentQuestionNumber(),
                    question = next.toUIM()
                )
            }
            currentQuestionStartTime = System.currentTimeMillis()
        }
    }

    protected open fun handleExitQuiz(navigateBack: () -> Unit) {
        _state.update { it.copy(showExitConfirmation = false) }
        if(quizEngine.getCurrentQuestionIndex() == 0) navigateBack()
        else setFinishedState()
    }

    protected open fun setFinishedState() {
        if(isAdsFree) {
            finishQuiz()
        } else {
            _state.update { it.copy(showAd = true) }
        }
    }

    private fun finishQuiz() {
        _state.update { it.copy(
            showAd = false,
            isQuizFinished = true,
            quizFinishedState = QuizFinishedState(
                seenQuestions = quizEngine.getAnsweredQuestions(),
                correctAnswers = quizEngine.getCorrectAnswers(),
                points = state.value.userScore,
                earnedPoints = earnedPoints,
                isStreakUpdated = isStreakUpdatedInSession,
                streak = useCases.getStreak() 
            )
        ) }
    }
}
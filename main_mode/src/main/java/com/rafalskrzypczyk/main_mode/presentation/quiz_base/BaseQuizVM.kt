package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.QuizType
import com.rafalskrzypczyk.core.analytics.ScreenName
import com.rafalskrzypczyk.core.analytics.analyticsName
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.FeedbackManager
import com.rafalskrzypczyk.core.report_issues.IssueReport
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.main_mode.domain.quiz_base.QuizEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.rafalskrzypczyk.core.utils.QuizSideEffect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseQuizVM (
    private val useCases: BaseQuizUseCases,
    protected val adHandler: QuizAdHandler,
    protected val feedbackManager: FeedbackManager,
    private val analyticsLogger: AnalyticsLogger,
    private val quizMode: QuizMode,
    private val quizType: QuizType,
    private val analyticsCategoryId: Long?,
    private val gameMode: String,
    private val enforceSingleSelection: Boolean = false
): ViewModel() {
    @Suppress("PropertyName")
    protected val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    protected val _effect = MutableSharedFlow<QuizSideEffect>()
    val effect = _effect.asSharedFlow()

    private val quizEngine = QuizEngine(useCases)

    protected var earnedPoints: Int = 0
    protected var isStreakUpdatedInSession: Boolean = false
    
    // Timing
    private var currentQuestionStartTime: Long = 0L

    private var hasLoggedQuizStarted = false
    private var hasLoggedQuizFinished = false
    private var hasLoggedQuizEnd = false
    private var exitedEarly = false

    // Seria poprawnych odpowiedzi pod rzad w tej sesji — parametr max_streak.
    private var currentAnswerStreak = 0
    private var sessionMaxStreak = 0

    init {
        loadUserScore()
        adHandler.initialize(viewModelScope)
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
            is MMQuizUIEvents.OnReportIssueDescriptionChanged -> _state.update { it.copy(reportIssueDescription = event.description) }
            MMQuizUIEvents.OnReportIssue -> reportIssue()
            MMQuizUIEvents.OnAdDismissed -> handleAdDismissed()
            MMQuizUIEvents.OnAdShown -> onAdShown()
        }
    }

    private fun handleAdDismissed() {
        _state.update { it.copy(showAd = false) }
        adHandler.handleAdDismissed(
            onContinue = { displayQuestion() },
            onFinish = { finishQuiz() }
        )
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
    
    private fun reportIssue() {
        val currentQ = state.value.question
        val description = state.value.reportIssueDescription
        val report = IssueReport(
            questionId = currentQ.id,
            questionContent = currentQ.questionText,
            description = description,
            gameMode = gameMode
        )
        viewModelScope.launch {
            useCases.reportIssue(report).collectLatest { response ->
                if(response is Response.Success) {
                    _state.update { it.copy(
                        showReportDialog = false, 
                        reportIssueDescription = ""
                    ) }
                    analyticsLogger.log(AnalyticsEvent.IssueReported(quizMode.analyticsName()))
                    feedbackManager.perform(FeedbackEvent.SUCCESS)
                    _effect.emit(QuizSideEffect.ShowReportSuccess)
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
        _state.update { it.copy(question = it.question.toggleAnswerSelection(answerId, enforceSingleSelection)) }
    }

    protected open fun submitAnswer() {
        // Przycisk zatwierdzania jest renderowany bezwarunkowo i tylko przyslaniany animacja,
        // wiec drugi tap w trakcie przejscia trafial tu ponownie i podwajal liczniki silnika.
        if (state.value.question.isAnswerSubmitted) return

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

        feedbackManager.perform(if (isCorrect) FeedbackEvent.ANSWER_CORRECT else FeedbackEvent.ANSWER_WRONG)

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

        trackAnswer(isCorrect)
    }

    /** Seria poprawnych odpowiedzi pod rzad — parametr `max_streak` w quiz_complete. */
    private fun trackAnswer(isCorrect: Boolean) {
        if (isCorrect) {
            currentAnswerStreak++
            sessionMaxStreak = maxOf(sessionMaxStreak, currentAnswerStreak)
        } else {
            currentAnswerStreak = 0
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
        logQuizStartedOnce()
        displayQuestion()
    }

    /**
     * [initializeQuiz] siedzi w collectLatest na flow z Firestore — kolejna emisja Success
     * reinicjalizuje sesję, więc bez flagi start poleciałby wielokrotnie na jedną sesję.
     */
    private fun logQuizStartedOnce() {
        if (hasLoggedQuizStarted) return
        hasLoggedQuizStarted = true

        analyticsLogger.log(
            AnalyticsEvent.QuizStarted(
                mode = quizMode.analyticsName(),
                quizType = quizType,
                questionCount = quizEngine.getQuestionsCount(),
                isFreePreview = false,
                categoryId = analyticsCategoryId,
                // Tylko razem z identyfikatorem: `categoryTitle` niesie tytul ekranu, wiec dla
                // Zadania dnia byla to zlokalizowana nazwa trybu, a nie zadna kategoria.
                categoryName = analyticsCategoryId?.let { _state.value.categoryTitle.ifBlank { null } },
            )
        )
    }

    /**
     * Logujemy na logicznym końcu sesji ([setFinishedState]), a nie w [finishQuiz]: w czterech
     * z pięciu trybów finalizację potrafi opóźnić interstitial, więc zdarzenie przepadłoby przy
     * ubiciu aplikacji w trakcie reklamy, a jej czas wliczyłby się w duration_sec.
     */
    private fun logQuizFinishedOnce() {
        // Wyjscie z ekranu, zanim pytania sie zaladuja, tez trafia tutaj (indeks silnika jest
        // wtedy zerowy). Bez tej bramki lecialby quiz_complete bez pasujacego quiz_start,
        // zawyzajac early_exit u uzytkownikow ze slabym polaczeniem.
        if (!hasLoggedQuizStarted) return
        if (hasLoggedQuizFinished) return
        hasLoggedQuizFinished = true

        val startTime = _state.value.quizStartTime
        analyticsLogger.log(
            AnalyticsEvent.QuizCompleted(
                mode = quizMode.analyticsName(),
                quizType = quizType,
                questionCount = quizEngine.getQuestionsCount(),
                answeredCount = quizEngine.getAnsweredQuestions(),
                correctCount = quizEngine.getCorrectAnswers(),
                isEarlyExit = exitedEarly,
                isFreePreview = false,
                durationSec = if (startTime == 0L) 0L else (System.currentTimeMillis() - startTime) / 1000,
                maxStreak = sessionMaxStreak,
                categoryId = analyticsCategoryId,
            )
        )
    }

    /**
     * Ekran wyniku to stan, nie trasa, wiec `screen_view` dla niego nie wyjdzie z NavHosta.
     * Osobno od quiz_complete: tamto zapada przed reklama, ten ekran pojawia sie po niej, a przy
     * wyjsciu przed pierwsza odpowiedzia nie pojawia sie wcale.
     */
    private fun logQuizEndScreenOnce() {
        if (hasLoggedQuizEnd) return
        hasLoggedQuizEnd = true
        analyticsLogger.log(AnalyticsEvent.ScreenView(ScreenName.QUIZ_END, mode = quizMode.analyticsName()))
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
            setFinishedState()
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
        if (next == null) logQuizFinishedOnce()
        if (adHandler.shouldShowAd(
                answeredCount = state.value.answeredQuestions.size,
                isQuizFinished = next == null,
                ignoreThreshold = state.value.isDailyExercise
            )
        ) {
            _state.update { it.copy(showAd = true) }
        } else {
            displayQuestion()
        }
    }

    protected open fun handleExitQuiz(navigateBack: () -> Unit) {
        _state.update { it.copy(showExitConfirmation = false) }
        exitedEarly = true
        if(quizEngine.getCurrentQuestionIndex() == 0) {
            // Wyjście przed pierwszą odpowiedzią nie finalizuje sesji, więc bez tego
            // quiz_start nie miałby zdarzenia terminalnego i lejek pokazywałby odpływ.
            logQuizFinishedOnce()
            navigateBack()
        }
        else setFinishedState()
    }

    protected open fun setFinishedState() {
        logQuizFinishedOnce()
        if (adHandler.shouldShowAd(
                answeredCount = state.value.answeredQuestions.size,
                isQuizFinished = true,
                ignoreThreshold = state.value.isDailyExercise
            )
        ) {
            _state.update { it.copy(showAd = true) }
        } else {
            finishQuiz()
        }
    }

    protected open fun finishQuiz() {
        logQuizFinishedOnce()
        logQuizEndScreenOnce()
        useCases.incrementCompletedQuizzes()
        feedbackManager.perform(FeedbackEvent.QUIZ_COMPLETED)
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
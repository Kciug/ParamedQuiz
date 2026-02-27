package com.rafalskrzypczyk.swipe_mode.presentation

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.report_issues.IssueReport
import com.rafalskrzypczyk.firestore.data.FirestoreCollections
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeUseCases
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SwipeModeSideEffect {
    object BuyMode : SwipeModeSideEffect
}

@HiltViewModel
class SwipeModeVM @Inject constructor(
    val useCases: SwipeModeUseCases,
    private val adHandler: QuizAdHandler,
    private val billingRepository: BillingRepository,
    private val premiumStatusProvider: PremiumStatusProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(SwipeModeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SwipeModeSideEffect>()
    val effect = _effect.asSharedFlow()

    private var isTrialActive: Boolean = savedStateHandle.get<Boolean>("isTrial") ?: false
    private var swipeModeProductDetails: AppProduct? = null

    private var questions: List<SwipeQuestion> = emptyList()
    private var currentQuestionIndex: Int = 0

    private var correctAnswers: Int = 0
    private var currentStreak: Int = 0
    private var bestStreak: Int = 0
    private var earnedPoints: Int = 0
    private var isStreakUpdatedInSession = false

    private var loadQuestionsJob: Job? = null
    private var questionsListenerJob: Job? = null

    // Timing stats
    private var quizStartTime: Long = 0L
    private var currentQuestionStartTime: Long = 0L
    private var totalResponseTimeAccumulator: Long = 0L

    // Error stats
    private var type1Errors: Int = 0
    private var type2Errors: Int = 0

    init {
        _state.update { it.copy(isTrial = isTrialActive) }
        adHandler.initialize(viewModelScope)
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { score ->
                _state.update { it.copy(userScore = score.score) }
            }
        }
        
        if (isTrialActive) {
            setupTrial()
        }

        loadQuestions()
    }

    private fun setupTrial() {
        billingRepository.startBillingConnection()
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                swipeModeProductDetails = products.find { it.id == BillingIds.ID_SWIPE_MODE }
                _state.update { it.copy(swipeModePrice = swipeModeProductDetails?.price) }
            }
        }
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(BillingIds.ID_SWIPE_MODE))
        }
        viewModelScope.launch {
            useCases.getQuestionsCount(FirestoreCollections.SWIPE_QUESTIONS).collectLatest { count ->
                _state.update { it.copy(totalSwipeModeQuestions = count) }
            }
        }
        viewModelScope.launch {
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                val swipeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_SWIPE_MODE)
                if (swipeUnlocked && isTrialActive) {
                    unlockFullMode()
                }
            }
        }
    }

    private fun unlockFullMode() {
        isTrialActive = false
        _state.update { it.copy(
            isTrial = false,
            showTrialFinishedPanel = false,
            responseState = ResponseState.Loading
        ) }
        loadQuestions()
    }

    private fun loadQuestions() {
        loadQuestionsJob?.cancel()
        loadQuestionsJob = viewModelScope.launch {
            val questionsFlow = if (isTrialActive) {
                useCases.getShuffledSwipeTrialQuestions()
            } else {
                useCases.getShuffledSwipeQuestions()
            }

            questionsFlow.collectLatest { response ->
                when (response) {
                    is Response.Error -> { _state.update { it.copy(responseState = ResponseState.Error(response.error)) }}
                    Response.Loading -> { _state.update { it.copy(responseState = ResponseState.Loading) }}
                    is Response.Success -> {
                        questions = response.data.reversed()
                        _state.update {
                            it.copy(
                                responseState = ResponseState.Success,
                                questionsCount = questions.size,
                                isLastAnswerFeedbackVisible = false
                            )
                        }
                        if (quizStartTime == 0L) quizStartTime = System.currentTimeMillis()
                        displayQuestion()
                        attachQuestionsListener()
                    }
                }
            }
        }
    }

    private fun attachQuestionsListener() {
        questionsListenerJob?.cancel()
        questionsListenerJob = viewModelScope.launch {
            val updatesFlow = if (isTrialActive) {
                useCases.getUpdatedSwipeTrialQuestions()
            } else {
                useCases.getUpdatedSwipeQuestions()
            }

            updatesFlow.collectLatest { newQuestions ->
                updateQuestionsData(newQuestions)
            }
        }
    }

    private fun updateQuestionsData(newQuestions: List<SwipeQuestion>) {
        val updatedQuestions = questions.map { oldQuestion ->
            newQuestions.find { it.id == oldQuestion.id } ?: oldQuestion
        }
        questions = updatedQuestions
        displayQuestion()
    }

    fun onEvent(event: SwipeModeUIEvents) {
        when (event) {
            is SwipeModeUIEvents.SubmitAnswer -> submitAnswer(event.questionId, event.isCorrect)
            is SwipeModeUIEvents.OnBackConfirmed -> handleExitQuiz(event.navigateBack)
            SwipeModeUIEvents.OnBackPressed -> _state.update { it.copy(showExitConfirmation = true) }
            SwipeModeUIEvents.OnBackDiscarded -> _state.update { it.copy(showExitConfirmation = false) }
            is SwipeModeUIEvents.ToggleReportDialog -> toggleReportDialog(event.show)
            is SwipeModeUIEvents.OnReportIssue -> reportIssue(event.description)
            SwipeModeUIEvents.OnAdDismissed -> handleAdDismissed()
            SwipeModeUIEvents.OnAdShown -> onAdShown()
            SwipeModeUIEvents.BuyMode -> buySwipeMode()
            is SwipeModeUIEvents.ExitTrial -> handleExitQuiz(event.navigateBack)
            SwipeModeUIEvents.OnFinalFeedbackFinished -> finalizeQuiz()
        }
    }

    private fun buySwipeMode() {
        if (swipeModeProductDetails != null) {
            viewModelScope.launch {
                _effect.emit(SwipeModeSideEffect.BuyMode)
            }
        }
    }

    fun launchBillingFlow(activity: Activity) {
        swipeModeProductDetails?.let {
            billingRepository.launchBillingFlow(activity, it)
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

    private fun toggleReportDialog(show: Boolean) {
        val indexToReport = if(currentQuestionIndex > 0) questions.size - currentQuestionIndex else questions.size - 1
        val content = if(questions.indices.contains(indexToReport)) questions[indexToReport].text else ""
        
        _state.update { it.copy(showReportDialog = show, reportableQuestionContent = content) }
    }

    private fun reportIssue(description: String) {
        val indexToReport = if(currentQuestionIndex > 0) questions.size - currentQuestionIndex else questions.size - 1
        if(questions.indices.contains(indexToReport)) {
            val q = questions[indexToReport]
            val report = IssueReport(
                questionId = q.id.toString(),
                questionContent = q.text,
                description = description,
                gameMode = "Swipe Mode"
            )
            viewModelScope.launch {
                useCases.reportIssue(report).collectLatest { response ->
                    if(response is Response.Success) {
                        _state.update { it.copy(showReportDialog = false, showReportSuccessToast = true) }
                    }
                }
            }
        }
    }

    private fun displayQuestion() {
        if(questions.indices.contains(currentQuestionIndex).not()) {
            if (isTrialActive && questions.isNotEmpty()) {
                _state.update { it.copy(showTrialFinishedPanel = true) }
            } else {
                setFinishedState()
            }
            return
        }

        currentQuestionStartTime = System.currentTimeMillis()

        val remainingQuestions = questions.subList(0, questions.size - currentQuestionIndex)
        _state.update { state ->
            state.copy(
                currentQuestionNumber = currentQuestionIndex + 1,
                questionsPair = remainingQuestions.takeLast(2).map { it.toPresentation() }
            )
        }
    }

    private fun displayNextQuestion() {
        currentQuestionIndex++
        val isAtEnd = currentQuestionIndex >= questions.size
        if (isAtEnd) {
            _state.update { it.copy(isLastAnswerFeedbackVisible = true) }
        } else {
            if (adHandler.shouldShowAd(
                    answeredCount = currentQuestionIndex,
                    isQuizFinished = false
                )
            ) {
                _state.update { it.copy(showAd = true) }
            } else {
                displayQuestion()
            }
        }
    }

    private fun finalizeQuiz() {
        _state.update { it.copy(
            isLastAnswerFeedbackVisible = false,
            answerResult = SwipeModeAnswerResult(result = SwipeQuizResult.NONE)
        ) }
        if (isTrialActive && questions.isNotEmpty()) {
            _state.update { it.copy(showTrialFinishedPanel = true) }
        } else {
            setFinishedState()
        }
    }

    private fun submitAnswer(questionId: Long, isCorrect: Boolean) {
        val now = System.currentTimeMillis()
        val duration = now - currentQuestionStartTime
        totalResponseTimeAccumulator += duration

        val answeredQuestion = questions.first { questionId == it.id }
        val answeredCorrectly = answeredQuestion.isCorrect == isCorrect

        if (!answeredCorrectly) {
            if (answeredQuestion.isCorrect) {
                type1Errors++
            } else {
                type2Errors++
            }
        }

        val answerResult = SwipeModeAnswerResult(
            questionId = questionId,
            result = if (answeredCorrectly) SwipeQuizResult.CORRECT else SwipeQuizResult.INCORRECT
        )

        if(answeredCorrectly) correctAnswers++

        _state.update { it.copy(
            answerResult = answerResult,
            correctAnswers = correctAnswers,
            type1Errors = type1Errors,
            type2Errors = type2Errors
        ) }

        updateStreak(answeredCorrectly)
        displayNextQuestion()
        earnedPoints += useCases.updateScore(questionId, answeredCorrectly)

        if (useCases.updateStreak()) {
            isStreakUpdatedInSession = true
        }
    }

    private fun updateStreak(isAnswerCorrect: Boolean) {
        if(isAnswerCorrect) {
            currentStreak++
            bestStreak = maxOf(currentStreak, bestStreak)
        } else {
            currentStreak = 0
        }
        _state.update { it.copy(currentStreak = currentStreak, bestStreak = bestStreak) }
    }

    private fun handleExitQuiz(navigateBack: () -> Unit) {
        _state.update { it.copy(showExitConfirmation = false) }
        if(currentQuestionIndex == 0) navigateBack()
        else setFinishedState()
    }

    private fun finishQuiz() {
        val totalDuration = System.currentTimeMillis() - quizStartTime
        val questionsAnswered = currentQuestionIndex
        val averageTime = if (questionsAnswered > 0) totalResponseTimeAccumulator / questionsAnswered else 0L

        _state.update { it.copy(
            showAd = false,
            isQuizFinished = true,
            averageResponseTime = averageTime,
            totalQuizDuration = totalDuration,
            quizFinishedState = QuizFinishedState(
                seenQuestions = currentQuestionIndex,
                correctAnswers = correctAnswers,
                points = state.value.userScore,
                earnedPoints = earnedPoints,
                isStreakUpdated = isStreakUpdatedInSession,
                streak = useCases.getStreak() 
            )
        ) }
    }

    private fun setFinishedState() {
        if (adHandler.shouldShowAd(
                answeredCount = currentQuestionIndex,
                isQuizFinished = true
            )
        ) {
            _state.update { it.copy(showAd = true) }
        } else {
            finishQuiz()
        }
    }
}

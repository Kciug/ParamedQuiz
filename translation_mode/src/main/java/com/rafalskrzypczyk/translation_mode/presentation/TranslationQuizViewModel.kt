package com.rafalskrzypczyk.translation_mode.presentation

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.FeedbackManager
import com.rafalskrzypczyk.firestore.data.FirestoreCollections
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM
import com.rafalskrzypczyk.translation_mode.domain.use_cases.TranslationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.rafalskrzypczyk.core.utils.QuizSideEffect

sealed interface TranslationModeSideEffect {
    object BuyMode : TranslationModeSideEffect
}

@HiltViewModel
class TranslationQuizViewModel @Inject constructor(
    private val useCases: TranslationUseCases,
    private val billingRepository: BillingRepository,
    private val premiumStatusProvider: PremiumStatusProvider,
    private val feedbackManager: FeedbackManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TranslationQuizState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<QuizSideEffect>()
    val effect = _effect.asSharedFlow()

    private val _billingEffect = MutableSharedFlow<TranslationModeSideEffect>()
    val billingEffect = _billingEffect.asSharedFlow()

    private var isTrialActive: Boolean = savedStateHandle.get<Boolean>("isTrial") ?: false
    private var translationModeProductDetails: AppProduct? = null

    private var loadDataJob: Job? = null
    private var questionsListenerJob: Job? = null

    private var earnedPointsSession: Int = 0
    private var isStreakUpdatedInSession: Boolean = false

    init {
        _state.update { it.copy(isTrial = isTrialActive) }

        viewModelScope.launch {
            billingRepository.purchaseResult.collectLatest { result ->
                when (result) {
                    is PurchaseResult.Success -> _state.update { it.copy(isPurchasing = false) }
                    is PurchaseResult.Pending -> _state.update { it.copy(isPurchasing = false) }
                    PurchaseResult.Cancelled -> _state.update { it.copy(isPurchasing = false) }
                    is PurchaseResult.Error -> _state.update { it.copy(isPurchasing = false, purchaseError = result.message) }
                }
            }
        }

        if (isTrialActive) {
            setupTrial()
        }

        loadData()
    }

    private fun setupTrial() {
        billingRepository.startBillingConnection()
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                translationModeProductDetails = products.find { it.id == BillingIds.ID_TRANSLATION_MODE }
                _state.update { it.copy(translationModePrice = translationModeProductDetails?.price) }
            }
        }
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(BillingIds.ID_TRANSLATION_MODE))
        }
        viewModelScope.launch {
            useCases.getQuestionsCount(FirestoreCollections.TRANSLATION_QUESTIONS).collectLatest { count ->
                _state.update { it.copy(totalTranslationQuestions = count) }
            }
        }
        viewModelScope.launch {
            combine(
                premiumStatusProvider.ownedProductIds,
                premiumStatusProvider.pendingProductIds
            ) { ownedIds, pendingIds ->
                ownedIds to pendingIds
            }.collectLatest { (ownedIds, pendingIds) ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                val translationUnlocked = hasFull || ownedIds.contains(BillingIds.ID_TRANSLATION_MODE)
                val isPending = pendingIds.contains(BillingIds.ID_FULL_PACKAGE) ||
                        pendingIds.contains(BillingIds.ID_TRANSLATION_MODE)

                if (translationUnlocked && isTrialActive) {
                    unlockFullMode()
                }

                _state.update { it.copy(isPending = isPending) }
            }
        }
    }

    private fun unlockFullMode() {
        isTrialActive = false
        _state.update {
            it.copy(
                isTrial = false,
                showTrialFinishedPanel = false,
                responseState = ResponseState.Loading
            )
        }
        loadData()
    }

    private fun loadData() {
        loadDataJob?.cancel()
        val questionsFlow = if (isTrialActive) {
            useCases.getTranslationTrialQuestions()
        } else {
            useCases.getTranslationQuestions()
        }
        loadDataJob = questionsFlow.onEach { response ->
            when (response) {
                is Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                is Response.Success -> {
                    val questions = response.data.shuffled().map { it.toUIM() }
                    _state.update {
                        it.copy(
                            responseState = ResponseState.Success,
                            questions = questions,
                            currentQuestionIndex = 0
                        )
                    }
                    attachQuestionsListener()
                }
            }
        }.launchIn(viewModelScope)

        useCases.getUserScore().onEach { userScore ->
            _state.update { it.copy(userScore = userScore.score) }
        }.launchIn(viewModelScope)
    }

    private fun attachQuestionsListener() {
        questionsListenerJob?.cancel()
        val updatesFlow = if (isTrialActive) {
            useCases.getUpdatedTranslationTrialQuestions()
        } else {
            useCases.getUpdatedTranslationQuestions()
        }
        questionsListenerJob = updatesFlow.onEach { newQuestionsDomain ->
            val currentQuestions = _state.value.questions
            val newQuestionsUIM = newQuestionsDomain.map { it.toUIM() }
            
            val updatedList = currentQuestions.map { currentQ ->
                val updatedQ = newQuestionsUIM.find { it.id == currentQ.id }
                updatedQ?.copy(
                    userAnswer = currentQ.userAnswer,
                    isAnswered = currentQ.isAnswered,
                    isCorrect = currentQ.isCorrect
                )
                    ?: currentQ
            }.toMutableList()

            val existingIds = currentQuestions.map { it.id }.toSet()
            val completelyNewQuestions = newQuestionsUIM.filter { it.id !in existingIds }
            
            if (completelyNewQuestions.isNotEmpty()) {
                updatedList.addAll(completelyNewQuestions)
            }

            _state.update { it.copy(questions = updatedList) }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: TranslationQuizEvents) {
        when (event) {
            is TranslationQuizEvents.OnAnswerChanged -> {
                val currentQuestions = _state.value.questions.toMutableList()
                val index = _state.value.currentQuestionIndex
                if (index in currentQuestions.indices) {
                    val currentQ = currentQuestions[index]
                    if (!currentQ.isAnswered) {
                        currentQuestions[index] = currentQ.copy(userAnswer = event.text)
                        _state.update { it.copy(questions = currentQuestions) }
                    }
                }
            }
            TranslationQuizEvents.OnSubmitAnswer -> submitAnswer()
            TranslationQuizEvents.OnNextQuestion -> nextQuestion()
            TranslationQuizEvents.OnBackDiscarded -> _state.update { it.copy(showExitConfirmation = false) }
            TranslationQuizEvents.OnBackPressed -> _state.update { it.copy(showExitConfirmation = true) }
            is TranslationQuizEvents.OnBackConfirmed -> handleExitQuiz(event.navigateBack)
            is TranslationQuizEvents.ToggleReportDialog -> _state.update { it.copy(showReportDialog = event.show) }
            is TranslationQuizEvents.OnReportIssueDescriptionChanged -> _state.update { it.copy(reportIssueDescription = event.description) }
            TranslationQuizEvents.OnReportIssue -> reportIssue()
            is TranslationQuizEvents.ToggleReviewDialog -> _state.update { it.copy(showReviewDialog = event.show) }
            TranslationQuizEvents.BuyMode -> buyMode()
            is TranslationQuizEvents.ExitTrial -> handleExitQuiz(event.navigateBack)
        }
    }

    private fun buyMode() {
        if (translationModeProductDetails != null) {
            _state.update { it.copy(isPurchasing = true, purchaseError = null) }
            viewModelScope.launch {
                _billingEffect.emit(TranslationModeSideEffect.BuyMode)
            }
        }
    }

    fun launchBillingFlow(activity: Activity) {
        translationModeProductDetails?.let {
            billingRepository.launchBillingFlow(activity, it)
        }
    }

    private fun handleExitQuiz(navigateBack: () -> Unit) {
        _state.update { it.copy(showExitConfirmation = false) }
        val state = _state.value
        val isStarted = state.currentQuestionIndex > 0 || state.questions.getOrNull(0)?.isAnswered == true
        
        if (!isStarted) {
            navigateBack()
        } else {
            finishQuiz()
        }
    }

    private fun submitAnswer() {
        val currentState = _state.value
        val index = currentState.currentQuestionIndex
        val currentQ = currentState.questions.getOrNull(index) ?: return
        
        if (currentQ.isAnswered) return

        val userAnswer = currentQ.userAnswer.trim()
        val isCorrect = currentQ.possibleTranslations.any { it.equals(userAnswer, ignoreCase = true) }

        feedbackManager.perform(if (isCorrect) FeedbackEvent.ANSWER_CORRECT else FeedbackEvent.ANSWER_WRONG)

        val updatedQuestions = currentState.questions.toMutableList()
        updatedQuestions[index] = currentQ.copy(
            userAnswer = userAnswer,
            isAnswered = true,
            isCorrect = isCorrect
        )

        val points = useCases.updateScoreWithQuestion(currentQ.id, isCorrect)
        earnedPointsSession += points
        val newCorrectCount = if (isCorrect) currentState.correctAnswersCount + 1 else currentState.correctAnswersCount

        if (isCorrect) {
            if (useCases.increaseStreakByQuestions()) {
                isStreakUpdatedInSession = true
            }
        }

        _state.update {
            it.copy(
                questions = updatedQuestions,
                correctAnswersCount = newCorrectCount
            )
        }
    }

    private fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < currentState.questions.size) {
            _state.update { it.copy(currentQuestionIndex = nextIndex) }
        } else if (isTrialActive && currentState.questions.isNotEmpty()) {
            _state.update { it.copy(showTrialFinishedPanel = true) }
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        useCases.incrementCompletedQuizzes()
        feedbackManager.perform(FeedbackEvent.QUIZ_COMPLETED)
        _state.update {
            it.copy(
                isQuizFinished = true,
                quizFinishedState = QuizFinishedState(
                    seenQuestions = it.questions.count { q -> q.isAnswered },
                    correctAnswers = it.correctAnswersCount,
                    points = it.userScore,
                    earnedPoints = earnedPointsSession,
                    isStreakUpdated = isStreakUpdatedInSession,
                    streak = useCases.getStreak()
                )
            )
        }
    }

    private fun reportIssue() {
        val currentQ = _state.value.currentQuestion ?: return
        val report = IssueReportDTO(
            questionId = currentQ.id,
            questionContent = currentQ.phrase,
            description = _state.value.reportIssueDescription,
            gameMode = "Translation Mode"
        )
        
        useCases.sendTranslationReport(report).onEach { response ->
             if (response is Response.Success) {
                 _state.update { it.copy(
                     showReportDialog = false, 
                     reportIssueDescription = ""
                 ) }
                 _effect.emit(QuizSideEffect.ShowReportSuccess)
             }
        }.launchIn(viewModelScope)
    }

    private fun TranslationQuestionDTO.toUIM(): TranslationQuestionUIM {
        return TranslationQuestionUIM(
            id = this.id,
            phrase = this.phrase.trim(),
            possibleTranslations = this.translations.map { it.trim() }.filter { it.isNotBlank() },
            isFree = this.isFree
        )
    }
}
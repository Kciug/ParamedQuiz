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

        // Wynik użytkownika nie zależy od stanu triala, więc kolektor zostaje poza loadData() -
        // inaczej przeładowanie po zakupie uruchamiałoby drugi, równoległy kolektor.
        useCases.getUserScore().onEach { userScore ->
            _state.update { it.copy(userScore = userScore.score) }
        }.launchIn(viewModelScope)

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
        loadData(preserveProgress = true)
    }

    /**
     * @param preserveProgress true przy przeładowaniu po wykupieniu trybu w trakcie triala.
     * Wtedy nie wolno ruszać pozycji w quizie ani odpowiedzi już udzielonych - nowe pytania
     * dochodzą do istniejącej kolejki przez [mergeQuestions]. Przy pierwszym wczytaniu
     * budujemy listę od zera.
     */
    private fun loadData(preserveProgress: Boolean = false) {
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
                    val incoming = response.data.map { it.toUIM() }
                    _state.update { state ->
                        if (preserveProgress) {
                            val merged = mergeQuestions(current = state.questions, incoming = incoming)
                            state.copy(
                                responseState = ResponseState.Success,
                                questions = merged,
                                currentQuestionIndex = resumeIndex(state.currentQuestionIndex, merged)
                            )
                        } else {
                            state.copy(
                                responseState = ResponseState.Success,
                                questions = incoming.shuffled(),
                                currentQuestionIndex = 0
                            )
                        }
                    }
                    attachQuestionsListener()
                }
            }
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
            val merged = mergeQuestions(
                current = _state.value.questions,
                incoming = newQuestionsDomain.map { it.toUIM() }
            )
            _state.update { it.copy(questions = merged) }
        }.launchIn(viewModelScope)
    }

    /**
     * Pozycja, od której wznawiamy quiz po odblokowaniu pełnego trybu.
     *
     * Panel trialówki pokazuje się, gdy [nextQuestion] nie miało już dokąd przejść - indeks
     * stoi wtedy na ostatnim odpowiedzianym pytaniu. Po zakupie trzeba dokończyć to przejście,
     * inaczej użytkownik wróciłby do pytania, na które właśnie odpowiedział.
     *
     * Gdy uprawnienie przychodzi w trakcie quizu (np. zakup pakietu na innym urządzeniu),
     * bieżące pytanie nie jest jeszcze odpowiedziane i zostajemy na miejscu.
     */
    private fun resumeIndex(current: Int, questions: List<TranslationQuestionUIM>): Int {
        val isCurrentAnswered = questions.getOrNull(current)?.isAnswered == true
        return if (isCurrentAnswered && current < questions.lastIndex) current + 1 else current
    }

    /**
     * Scala nową listę pytań z obecną, zachowując pozycję i odpowiedzi użytkownika.
     * Pytania dopasowywane są po [TranslationQuestionUIM.id] - treść bierzemy z nowej listy,
     * stan odpowiedzi ze starej. Pytania o nieznanych id trafiają na koniec kolejki.
     *
     * Używane w dwóch miejscach: przy zdalnej aktualizacji treści i przy przeładowaniu
     * po wykupieniu trybu w trakcie triala.
     */
    private fun mergeQuestions(
        current: List<TranslationQuestionUIM>,
        incoming: List<TranslationQuestionUIM>
    ): List<TranslationQuestionUIM> {
        val refreshed = current.map { currentQ ->
            incoming.find { it.id == currentQ.id }?.copy(
                userAnswer = currentQ.userAnswer,
                isAnswered = currentQ.isAnswered,
                isCorrect = currentQ.isCorrect
            ) ?: currentQ
        }

        val existingIds = current.mapTo(mutableSetOf()) { it.id }
        // Tasujemy doklejany blok - z Firestore przychodzi w stałej kolejności,
        // a tryb normalnie prezentuje pytania losowo.
        return refreshed + incoming.filter { it.id !in existingIds }.shuffled()
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
                feedbackManager.perform(FeedbackEvent.STREAK_UP)
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
                 feedbackManager.perform(FeedbackEvent.SUCCESS)
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
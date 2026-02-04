package com.rafalskrzypczyk.translation_mode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM
import com.rafalskrzypczyk.translation_mode.domain.use_cases.TranslationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TranslationQuizViewModel @Inject constructor(
    private val useCases: TranslationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(TranslationQuizState())
    val state = _state.asStateFlow()

    private var earnedPointsSession: Int = 0

    init {
        loadData()
    }

    private fun loadData() {
        useCases.getTranslationQuestions().onEach { response ->
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
        useCases.getUpdatedTranslationQuestions().onEach { newQuestionsDomain ->
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
            is TranslationQuizEvents.ToggleReviewDialog -> _state.update { it.copy(showReviewDialog = event.show) }
            is TranslationQuizEvents.OnReportIssue -> reportIssue(event.description)
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

        val updatedQuestions = currentState.questions.toMutableList()
        updatedQuestions[index] = currentQ.copy(isAnswered = true, isCorrect = isCorrect)

        val points = useCases.updateScoreWithQuestion(currentQ.id, isCorrect)
        earnedPointsSession += points
        val newCorrectCount = if (isCorrect) currentState.correctAnswersCount + 1 else currentState.correctAnswersCount

        if (isCorrect) {
            useCases.increaseStreakByQuestions()
        }

        _state.update {
            it.copy(
                questions = updatedQuestions,
                correctAnswersCount = newCorrectCount
                // userScore will be updated via flow from UseCases
            )
        }
    }

    private fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < currentState.questions.size) {
            _state.update { it.copy(currentQuestionIndex = nextIndex) }
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        _state.update {
            it.copy(
                isQuizFinished = true,
                quizFinishedState = QuizFinishedState(
                    seenQuestions = it.questions.count { q -> q.isAnswered },
                    correctAnswers = it.correctAnswersCount,
                    points = it.userScore, // Total User Score
                    earnedPoints = earnedPointsSession // Points earned in this session
                )
            )
        }
    }

    private fun reportIssue(description: String) {
        val currentQ = _state.value.currentQuestion ?: return
        val report = IssueReportDTO(
            questionId = currentQ.id.toString(),
            questionContent = currentQ.phrase,
            description = description,
            gameMode = "Translation Mode"
        )
        
        useCases.sendTranslationReport(report).onEach { response ->
             if (response is Response.Success) {
                 _state.update { it.copy(showReportDialog = false, showReportSuccessToast = true) }
             }
        }.launchIn(viewModelScope)
    }

    private fun TranslationQuestionDTO.toUIM(): TranslationQuestionUIM {
        return TranslationQuestionUIM(
            id = this.id,
            phrase = this.phrase,
            possibleTranslations = this.translations
        )
    }
}
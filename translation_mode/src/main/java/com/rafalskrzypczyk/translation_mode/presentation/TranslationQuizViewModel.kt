package com.rafalskrzypczyk.translation_mode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TranslationQuizViewModel @Inject constructor(
    private val firestoreApi: FirestoreApi
) : ViewModel() {

    private val _state = MutableStateFlow(TranslationQuizState())
    val state = _state.asStateFlow()

    init {
        fetchQuestions()
    }

    private fun fetchQuestions() {
        firestoreApi.getTranslationQuestions().onEach { response ->
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
                }
            }
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
            is TranslationQuizEvents.OnBackConfirmed -> event.navigateBack()
            is TranslationQuizEvents.ToggleReportDialog -> _state.update { it.copy(showReportDialog = event.show) }
            is TranslationQuizEvents.OnReportIssue -> reportIssue(event.description)
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

        val newScore = if (isCorrect) currentState.userScore + 10 else currentState.userScore
        val newCorrectCount = if (isCorrect) currentState.correctAnswersCount + 1 else currentState.correctAnswersCount

        _state.update {
            it.copy(
                questions = updatedQuestions,
                userScore = newScore,
                correctAnswersCount = newCorrectCount
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
        val state = _state.value
        val totalQuestions = state.questions.size
        val correctAnswers = state.correctAnswersCount
        val score = state.userScore

        // Simple grading logic
        val percentage = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) * 100 else 0f
        val passed = percentage >= 50 // Example threshold

        _state.update {
            it.copy(
                isQuizFinished = true,
                quizFinishedState = QuizFinishedState(
                    seenQuestions = totalQuestions,
                    correctAnswers = correctAnswers,
                    points = score,
                    earnedPoints = score
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
            gameMode = "translation_mode"
        )
        
        firestoreApi.sendIssueReport(report).onEach { response ->
             if (response is Response.Success) {
                 _state.update { it.copy(showReportDialog = false, showReportSuccessToast = true) }
                 // Reset toast flag after consumption in UI? or rely on LaunchedEffect there
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
package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeUseCases
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwipeModeVM @Inject constructor(
    val useCases: SwipeModeUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(SwipeModeState())
    val state = _state.asStateFlow()

    private var questions: List<SwipeQuestion> = emptyList()
    private var currentQuestionIndex: Int = 0

    private var currentStreak: Int = 0
    private var bestStreak: Int = 0

    init {
        viewModelScope.launch {
            useCases.getShuffledSwipeQuestions().collectLatest { response ->
                when (response) {
                    is Response.Error -> { _state.update { it.copy(responseState = ResponseState.Error(response.error)) }}
                    Response.Loading -> { _state.update { it.copy(responseState = ResponseState.Loading) }}
                    is Response.Success -> {
                        questions = response.data.reversed()
                        _state.update {
                            it.copy(
                                responseState = ResponseState.Success,
                                questionsCount = questions.size
                            )
                        }
                        displayQuestion()
                    }
                }
            }
        }
    }

    fun onEvent(event: SwipeModeUIEvents) {
        when (event) {
            is SwipeModeUIEvents.SubmitAnswer -> submitAnswer(event.questionId, event.isCorrect)
        }
    }

    private fun displayQuestion() {
        if(questions.indices.contains(currentQuestionIndex).not()) {
            _state.update { it.copy(isQuizFinished = true) }
            return
        }

        val remainingQuestions = questions.subList(0, questions.size - currentQuestionIndex)
        _state.update {
            it.copy(
                currentQuestionNumber = currentQuestionIndex + 1,
                questionsPair = remainingQuestions.takeLast(2).map { it.toPresentation() },
                answerResult = SwipeQuizResult.NONE
            )
        }
    }

    private fun displayNextQuestion() {
        currentQuestionIndex++
        displayQuestion()
    }

    private fun submitAnswer(questionId: Long, isCorrect: Boolean) {
        val answeredQuestion = questions.first { questionId == it.id }
        if(answeredQuestion.isCorrect == isCorrect) {
            _state.update { it.copy(
                answerResult = SwipeQuizResult.CORRECT,
                correctAnswers = it.correctAnswers + 1
            ) }
            updateStreak(true)
        } else {
            _state.update { it.copy(answerResult = SwipeQuizResult.INCORRECT) }
            updateStreak(false)
        }
        displayNextQuestion()
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
}
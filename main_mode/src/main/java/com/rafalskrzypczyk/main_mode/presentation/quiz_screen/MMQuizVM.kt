package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz.MMQuizUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String

@HiltViewModel
class MMQuizVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: MMQuizUseCases
): ViewModel(){
    private val _state = MutableStateFlow(MMQuizState())
    val state = _state.asStateFlow()

    private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: -1
    private val categoryTitle: String = savedStateHandle.get<String>("categoryTitle") ?: ""

    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex: Int = 0

    init {
        viewModelScope.launch {
            useCases.getQuestionsForCategory(categoryId).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        questions = response.data
                        attachQuestionsListener()
                        initializeView()
                    }
                    is Response.Error -> { _state.update { it.copy(responseState = ResponseState.Error(response.error)) }}
                    Response.Loading -> { _state.update { it.copy(responseState = ResponseState.Loading) }}
                }
            }
        }
    }

    fun onEvent(event: MMQuizUIEvents) {
        when (event) {
            MMQuizUIEvents.OnBackPressed -> onBackPressed()
            MMQuizUIEvents.OnBackDiscarded -> onBackDiscarded()
            is MMQuizUIEvents.OnAnswerClicked -> onAnswerClicked(event.answerId)
            MMQuizUIEvents.OnSubmitAnswer -> submitAnswer()
            MMQuizUIEvents.OnNextQuestion -> displayNextQuestion()
        }
    }

    private fun attachQuestionsListener() {
        viewModelScope.launch {
            val updatedQuestions = mutableListOf<Question>()
            useCases.getUpdatedQuestions().collectLatest { data ->
                questions.forEach { question ->
                    val updatedQuestion = data.firstOrNull { it.id == question.id }
                    if(updatedQuestion != null) {
                        updatedQuestions.add(updatedQuestion)
                    } else {
                        updatedQuestions.add(question)
                    }
                }
                questions = updatedQuestions
                displayQuestion()
            }
        }
    }

    private fun initializeView() {
        _state.update {
            it.copy(
                responseState = ResponseState.Success,
                categoryTitle = categoryTitle,
                questionsCount = questions.size
            )
        }
        displayQuestion()
    }

    private fun displayQuestion() {
        if(questions.indices.contains(currentQuestionIndex).not()) {
            _state.update { it.copy(isQuizFinished = true) }
            return
        }

        val nextQuestion = questions[currentQuestionIndex]
        _state.update {
            it.copy(
                currentQuestionNumber = currentQuestionIndex + 1,
                questionText = nextQuestion.questionText,
                answers = nextQuestion.answers.map { answer -> answer.toUIM() },
                isAnswerSubmitted = false,
                isAnswerCorrect = false,
                correctAnswers = emptyList(),
            )
        }
    }

    private fun displayNextQuestion() {
        currentQuestionIndex++
        displayQuestion()
    }

    private fun onBackPressed() {
        _state.update { it.copy(showExitConfirmation = true) }
    }

    private fun onBackDiscarded() {
        _state.update { it.copy(showExitConfirmation = false) }
    }

    private fun onAnswerClicked(answerId: Long) {
        val updatedAnswers = state.value.answers.map { answer ->
            if(answer.id == answerId) answer.copy(isSelected = !answer.isSelected)
            else answer
        }

        _state.update { it.copy(answers = updatedAnswers) }
    }

    private fun submitAnswer() {
        val isAnswerCorrect = evaluateAnswers()
        _state.update {
            it.copy(
                isAnswerSubmitted = true,
                isAnswerCorrect = isAnswerCorrect,
                correctAnswers = questions[currentQuestionIndex].answers.filter { it.isCorrect }.map { it.answerText }
            )
        }
    }

    private fun evaluateAnswers() : Boolean {
        val selectedAnswers = state.value.answers.filter { it.isSelected }
        val correctAnswers = questions[currentQuestionIndex].answers.filter { it.isCorrect }

        if(selectedAnswers.size != correctAnswers.size) return false
        correctAnswers.forEach { correctAnswer ->
            if(selectedAnswers.none { it.id == correctAnswer.id }) return false
        }
        return true
    }
}
package com.rafalskrzypczyk.main_mode.presentation

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.main_mode.domain.QuizEngine
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz.MMQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.MMQuizState
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.MMQuizUIEvents
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.submitAnswer
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.toUIM
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.updateAnswers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseQuizVM (
    private val quizEngine: QuizEngine,
    private val useCases: MMQuizUseCases
): ViewModel() {
    protected val _state = MutableStateFlow(MMQuizState())
    val state = _state.asStateFlow()

    abstract suspend fun loadQuestions()

    fun onEvent(event: MMQuizUIEvents) {
        when (event) {
            MMQuizUIEvents.OnBackPressed -> _state.update { it.copy(showExitConfirmation = true) }
            MMQuizUIEvents.OnBackDiscarded -> _state.update { it.copy(showExitConfirmation = false) }
            is MMQuizUIEvents.OnAnswerClicked -> onAnswerClicked(event.answerId)
            MMQuizUIEvents.OnSubmitAnswer -> submitAnswer()
            MMQuizUIEvents.OnNextQuestion -> displayNextQuestion()
        }
    }

    private fun onAnswerClicked(answerId: Long) {
        val updatedAnswers = state.value.question.answers.map { answer ->
            if (answer.id == answerId) answer.copy(isSelected = !answer.isSelected)
            else answer
        }
        _state.update { it.copy(question = it.question.updateAnswers(updatedAnswers)) }
    }

    private fun submitAnswer() {
        val selected = state.value.question.answers.filter { it.isSelected }.map { it.id }
        val isCorrect = quizEngine.submitAnswer(selected)

        _state.update {
            it.copy(
                question = it.question.submitAnswer(isCorrect),
                correctAnswers = quizEngine.getCorrectAnswers()
            )
        }

        val currentQ = quizEngine.getCurrentQuestion()
        if (currentQ != null) {
            useCases.updateScore(currentQ.id, isCorrect)
            useCases.updateStreak()
        }
    }

    protected fun initializeQuiz(questions: List<Question>, title: String) {
        quizEngine.setQuestions(questions)
        _state.update {
            it.copy(
                responseState = ResponseState.Success,
                categoryTitle = title,
                questionsCount = quizEngine.getQuestionsCount()
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
        _state.update {
            it.copy(
                currentQuestionNumber = quizEngine.getCurrentQuestionNumber(),
                question = q.toUIM()
            )
        }
    }

    private fun displayNextQuestion() {
        val next = quizEngine.nextQuestion()
        if (next == null) {
            _state.update { it.copy(isQuizFinished = true) }
        } else {
            _state.update {
                it.copy(
                    currentQuestionNumber = quizEngine.getCurrentQuestionNumber(),
                    question = next.toUIM()
                )
            }
        }
    }
}
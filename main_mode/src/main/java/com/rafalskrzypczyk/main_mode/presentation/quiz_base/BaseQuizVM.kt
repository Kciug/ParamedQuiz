package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.main_mode.domain.quiz_base.QuizEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseQuizVM (
    private val useCases: BaseQuizUseCases
): ViewModel() {
    protected val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    private val quizEngine = QuizEngine(useCases)

    protected var earnedPoints: Int = 0

    init {
        loadUserScore()
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
        }
    }

    protected fun loadUserScore() {
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { score ->
                _state.update { it.copy(userScore = score.score) }
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
            earnedPoints += useCases.updateScore(currentQ.id, isCorrect)
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
        }
    }

    protected open fun handleExitQuiz(navigateBack: () -> Unit) {
        _state.update { it.copy(showExitConfirmation = false) }
        if(quizEngine.getCurrentQuestionIndex() == 0) navigateBack()
        else setFinishedState()
    }

    protected open fun setFinishedState() {
        _state.update { it.copy(
            isQuizFinished = true,
            quizFinishedState = QuizFinishedState(
                seenQuestions = quizEngine.getAnsweredQuestions(),
                correctAnswers = quizEngine.getCorrectAnswers(),
                points = state.value.userScore,
                earnedPoints = earnedPoints
            )
        ) }
    }
}
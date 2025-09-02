package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import com.rafalskrzypczyk.main_mode.domain.models.Question

data class QuestionUIM(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<AnswerUIM> = emptyList(),
    val isAnswerSubmitted: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val correctAnswers: List<String> = emptyList(),
)

fun Question.toUIM() : QuestionUIM = QuestionUIM(
    id = id,
    questionText = questionText,
    answers = answers.map { it.toUIM() },
    correctAnswers = answers.filter { it.isCorrect }.map { it.answerText }
)

fun QuestionUIM.submitAnswer(answeredCorrectly: Boolean) : QuestionUIM = copy(
    isAnswerSubmitted = true,
    isAnswerCorrect = answeredCorrectly
)

fun QuestionUIM.updateAnswers(answers: List<AnswerUIM>) : QuestionUIM = copy(answers = answers)
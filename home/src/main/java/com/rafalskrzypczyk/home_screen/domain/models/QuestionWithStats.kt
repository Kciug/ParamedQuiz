package com.rafalskrzypczyk.home_screen.domain.models

import com.rafalskrzypczyk.score.domain.QuestionAnnotation

data class QuestionWithStats(
    val id: Long,
    val question: String,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val correctPercentage: Int = ((correctAnswers.toFloat() / (correctAnswers + wrongAnswers)) * 100).toInt()
)

fun combineQuestionsWithStats(
    questions: List<SimpleQuestion>,
    annotations: List<QuestionAnnotation>
): List<QuestionWithStats> {
    val statsById = annotations.associateBy { it.questionId }

    return questions.mapNotNull { question ->
        val stats = statsById[question.id]
        stats?.let {
            QuestionWithStats(
                id = question.id,
                question = question.questionText,
                correctAnswers = it.timesCorrect.toInt(),
                wrongAnswers = it.timesIncorrect.toInt()
            )
        }
    }
}
package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.main_mode.domain.models.Question

@Immutable
data class QuestionUIM(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<AnswerUIM> = emptyList(),
    val isAnswerSubmitted: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val correctAnswers: List<String> = emptyList(),
    val correctAnswerIds: List<Long> = emptyList(), // Nowe pole
    val userPrecision: Int = 0 // Nowe pole: zapisana precyzja dla tego pytania
)

fun Question.toUIM() : QuestionUIM = QuestionUIM(
    id = id,
    questionText = questionText,
    answers = answers.map { it.toUIM() },
    correctAnswers = answers.filter { it.isCorrect }.map { it.answerText },
    correctAnswerIds = answers.filter { it.isCorrect }.map { it.id }
)

fun QuestionUIM.submitAnswer(answeredCorrectly: Boolean, precision: Int) : QuestionUIM = copy(
    isAnswerSubmitted = true,
    isAnswerCorrect = answeredCorrectly,
    userPrecision = precision
)

fun QuestionUIM.updateAnswers(answers: List<AnswerUIM>) : QuestionUIM = copy(answers = answers)

val QuestionUIM.isMultipleChoice: Boolean
    get() = correctAnswerIds.size > 1

fun QuestionUIM.toggleAnswerSelection(answerId: Long, enforceSingleSelection: Boolean) : QuestionUIM {
    val clickedAnswer = answers.firstOrNull { it.id == answerId } ?: return this
    val shouldDeselectOthers = enforceSingleSelection && !isMultipleChoice && !clickedAnswer.isSelected
    val updatedAnswers = answers.map { answer ->
        when {
            answer.id == answerId -> if (answer.isSelected) answer.makeDeselected() else answer.makeSelected()
            shouldDeselectOthers -> answer.makeDeselected()
            else -> answer
        }
    }
    return updateAnswers(updatedAnswers)
}

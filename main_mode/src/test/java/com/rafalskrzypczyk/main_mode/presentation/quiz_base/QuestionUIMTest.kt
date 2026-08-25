package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionUIMTest {

    private val answers = listOf(
        AnswerUIM(id = 1, answerText = "A"),
        AnswerUIM(id = 2, answerText = "B"),
        AnswerUIM(id = 3, answerText = "C")
    )

    private fun singleChoiceQuestion() = QuestionUIM(
        id = 100,
        questionText = "Pytanie",
        answers = answers,
        correctAnswerIds = listOf(1)
    )

    private fun multipleChoiceQuestion() = QuestionUIM(
        id = 200,
        questionText = "Pytanie",
        answers = answers,
        correctAnswerIds = listOf(1, 2)
    )

    private fun QuestionUIM.selectedIds() = answers.filter { it.isSelected }.map { it.id }

    @Test
    fun `single choice with enforcement keeps only last selected answer`() {
        val result = singleChoiceQuestion()
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)
            .toggleAnswerSelection(answerId = 2, enforceSingleSelection = true)

        assertEquals(listOf(2L), result.selectedIds())
    }

    @Test
    fun `single choice with enforcement deselects answer on second click`() {
        val result = singleChoiceQuestion()
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)

        assertTrue(result.selectedIds().isEmpty())
    }

    @Test
    fun `multiple choice with enforcement still allows many selected answers`() {
        val result = multipleChoiceQuestion()
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)
            .toggleAnswerSelection(answerId = 3, enforceSingleSelection = true)

        assertEquals(listOf(1L, 3L), result.selectedIds())
    }

    @Test
    fun `multiple choice with enforcement deselects only the clicked answer`() {
        val result = multipleChoiceQuestion()
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)
            .toggleAnswerSelection(answerId = 3, enforceSingleSelection = true)
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)

        assertEquals(listOf(3L), result.selectedIds())
    }

    @Test
    fun `single choice without enforcement still allows many selected answers`() {
        val result = singleChoiceQuestion()
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = false)
            .toggleAnswerSelection(answerId = 2, enforceSingleSelection = false)

        assertEquals(listOf(1L, 2L), result.selectedIds())
    }

    @Test
    fun `question without correct answers is treated as single choice`() {
        val question = QuestionUIM(answers = answers, correctAnswerIds = emptyList())

        val result = question
            .toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)
            .toggleAnswerSelection(answerId = 2, enforceSingleSelection = true)

        assertEquals(listOf(2L), result.selectedIds())
    }

    @Test
    fun `unknown answer id leaves selection untouched`() {
        val question = singleChoiceQuestion().toggleAnswerSelection(answerId = 1, enforceSingleSelection = true)

        val result = question.toggleAnswerSelection(answerId = 99, enforceSingleSelection = true)

        assertEquals(listOf(1L), result.selectedIds())
    }

    @Test
    fun `isMultipleChoice reflects correct answers count`() {
        assertFalse(singleChoiceQuestion().isMultipleChoice)
        assertTrue(multipleChoiceQuestion().isMultipleChoice)
    }
}

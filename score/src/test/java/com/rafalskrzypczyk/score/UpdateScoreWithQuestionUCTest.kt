package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.ScorePoints
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateScoreWithQuestionUCTest {
    private lateinit var scoreManager: ScoreManager
    private lateinit var updateScoreWithQuestionUC: UpdateScoreWithQuestionUC

    @Before
    fun setup() {
        scoreManager = mockk(relaxed = true)
        updateScoreWithQuestionUC = UpdateScoreWithQuestionUC(scoreManager)
    }

    @Test
    fun `should add new question with correct answer and grant FIRST_CORRECT points`() {
        // given
        every { scoreManager.getScore() } returns Score(0, 0,  null, null, emptyList())

        // when
        updateScoreWithQuestionUC(questionId = 101L, answeredCorrectly = true)

        // then
        val slot = slot<Score>()
        verify(exactly = 1) { scoreManager.updateScore(capture(slot)) }

        val updated = slot.captured
        assertEquals(ScorePoints.FIRST_CORRECT, updated.score)
        assertEquals(1, updated.seenQuestions.size)

        with(updated.seenQuestions.first()) {
            assertEquals(101L, questionId)
            assertEquals(1, timesSeen)
            assertEquals(1, timesCorrect)
        }
    }

    @Test
    fun `should update existing question and grant CORRECT points`() {
        // given
        val existingQuestion = QuestionAnnotation(123L, timesSeen = 1, timesCorrect = 1)
        every { scoreManager.getScore() } returns Score(10, 0, null, null, listOf(existingQuestion))

        // when
        updateScoreWithQuestionUC(123L, answeredCorrectly = true)

        // then
        val slot = slot<Score>()
        verify { scoreManager.updateScore(capture(slot)) }

        val updated = slot.captured
        assertEquals((10 + ScorePoints.CORRECT), updated.score)
        val q = updated.seenQuestions.first()
        assertEquals(2, q.timesSeen)
        assertEquals(2, q.timesCorrect)
    }

    @Test
    fun `should update existing question and not grant points on wrong answer`() {
        val existing = QuestionAnnotation(321L, timesSeen = 2, timesCorrect = 0)
        every { scoreManager.getScore() } returns Score(5, 0, null, null, listOf(existing))

        updateScoreWithQuestionUC(321L, answeredCorrectly = false)

        val slot = slot<Score>()
        verify { scoreManager.updateScore(capture(slot)) }

        val updated = slot.captured
        assertEquals(5, updated.score)
        val q = updated.seenQuestions.first()
        assertEquals(3, q.timesSeen)
        assertEquals(0, q.timesCorrect)
    }

    @Test
    fun `should add new incorrect question and not grant points`() {
        every { scoreManager.getScore() } returns Score(7, 0,null, null, emptyList())

        updateScoreWithQuestionUC(456L, answeredCorrectly = false)

        val slot = slot<Score>()
        verify { scoreManager.updateScore(capture(slot)) }

        val updated = slot.captured
        assertEquals(7, updated.score)
        assertEquals(1, updated.seenQuestions.size)

        with(updated.seenQuestions.first()) {
            assertEquals(456L, questionId)
            assertEquals(1, timesSeen)
            assertEquals(0, timesCorrect)
        }
    }
}
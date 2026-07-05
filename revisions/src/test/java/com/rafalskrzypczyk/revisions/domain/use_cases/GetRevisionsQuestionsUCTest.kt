package com.rafalskrzypczyk.revisions.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.revisions.domain.RevisionsRepository
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRevisionsQuestionsUCTest {

    private lateinit var repository: RevisionsRepository
    private lateinit var scoreManager: ScoreManager
    private lateinit var useCase: GetRevisionsQuestionsUC

    @Before
    fun setup() {
        repository = mockk()
        scoreManager = mockk()
        useCase = GetRevisionsQuestionsUC(repository, scoreManager)
    }

    @Test
    fun `should only return questions that have been answered at least once`() = runTest {
        val q1 = Question(id = 1L, questionText = "Q1")
        val q2 = Question(id = 2L, questionText = "Q2")
        val q3 = Question(id = 3L, questionText = "Q3")

        val questions = listOf(
            RevisionQuestion.Main(q1),
            RevisionQuestion.Main(q2),
            RevisionQuestion.Main(q3)
        )

        val seen = listOf(
            QuestionAnnotation(questionId = 1L, timesSeen = 5, timesCorrect = 3),
            QuestionAnnotation(questionId = 3L, timesSeen = 0, timesCorrect = 0)
        )

        every { repository.getQuestions(any(), any()) } returns flowOf(Response.Success(questions))
        every { scoreManager.getScore() } returns Score(0, 0, null, null, seen)

        val result = useCase(QuizMode.MainMode, categoryId = null, criterion = RevisionCriterion.WORST, limit = null).first()

        assertTrue(result is Response.Success)
        val data = (result as Response.Success).data
        assertEquals(1, data.size)
        assertEquals(1L, data[0].id)
    }

    @Test
    fun `should sort by WORST correctly`() = runTest {
        val q1 = Question(id = 1L, questionText = "Q1")
        val q2 = Question(id = 2L, questionText = "Q2")
        val q3 = Question(id = 3L, questionText = "Q3")

        val questions = listOf(
            RevisionQuestion.Main(q1),
            RevisionQuestion.Main(q2),
            RevisionQuestion.Main(q3)
        )

        val seen = listOf(
            QuestionAnnotation(questionId = 1L, timesSeen = 10, timesCorrect = 8), // 80%
            QuestionAnnotation(questionId = 2L, timesSeen = 10, timesCorrect = 2), // 20%
            QuestionAnnotation(questionId = 3L, timesSeen = 10, timesCorrect = 5)  // 50%
        )

        every { repository.getQuestions(any(), any()) } returns flowOf(Response.Success(questions))
        every { scoreManager.getScore() } returns Score(0, 0, null, null, seen)

        val result = useCase(QuizMode.MainMode, categoryId = null, criterion = RevisionCriterion.WORST, limit = null).first()

        assertTrue(result is Response.Success)
        val data = (result as Response.Success).data
        assertEquals(3, data.size)
        assertEquals(2L, data[0].id)
        assertEquals(3L, data[1].id)
        assertEquals(1L, data[2].id)
    }

    @Test
    fun `should sort by BEST correctly`() = runTest {
        val q1 = Question(id = 1L, questionText = "Q1")
        val q2 = Question(id = 2L, questionText = "Q2")
        val q3 = Question(id = 3L, questionText = "Q3")

        val questions = listOf(
            RevisionQuestion.Main(q1),
            RevisionQuestion.Main(q2),
            RevisionQuestion.Main(q3)
        )

        val seen = listOf(
            QuestionAnnotation(questionId = 1L, timesSeen = 10, timesCorrect = 8), // 80%
            QuestionAnnotation(questionId = 2L, timesSeen = 10, timesCorrect = 2), // 20%
            QuestionAnnotation(questionId = 3L, timesSeen = 10, timesCorrect = 5)  // 50%
        )

        every { repository.getQuestions(any(), any()) } returns flowOf(Response.Success(questions))
        every { scoreManager.getScore() } returns Score(0, 0, null, null, seen)

        val result = useCase(QuizMode.MainMode, categoryId = null, criterion = RevisionCriterion.BEST, limit = null).first()

        assertTrue(result is Response.Success)
        val data = (result as Response.Success).data
        assertEquals(3, data.size)
        assertEquals(1L, data[0].id)
        assertEquals(3L, data[1].id)
        assertEquals(2L, data[2].id)
    }

    @Test
    fun `should filter by UNDER_50 correctly`() = runTest {
        val q1 = Question(id = 1L, questionText = "Q1")
        val q2 = Question(id = 2L, questionText = "Q2")
        val q3 = Question(id = 3L, questionText = "Q3")

        val questions = listOf(
            RevisionQuestion.Main(q1),
            RevisionQuestion.Main(q2),
            RevisionQuestion.Main(q3)
        )

        val seen = listOf(
            QuestionAnnotation(questionId = 1L, timesSeen = 10, timesCorrect = 8), // 80%
            QuestionAnnotation(questionId = 2L, timesSeen = 10, timesCorrect = 2), // 20%
            QuestionAnnotation(questionId = 3L, timesSeen = 10, timesCorrect = 5)  // 50%
        )

        every { repository.getQuestions(any(), any()) } returns flowOf(Response.Success(questions))
        every { scoreManager.getScore() } returns Score(0, 0, null, null, seen)

        val result = useCase(QuizMode.MainMode, categoryId = null, criterion = RevisionCriterion.UNDER_50, limit = null).first()

        assertTrue(result is Response.Success)
        val data = (result as Response.Success).data
        assertEquals(1, data.size)
        assertEquals(2L, data[0].id)
    }

    @Test
    fun `should respect limit correctly`() = runTest {
        val q1 = Question(id = 1L, questionText = "Q1")
        val q2 = Question(id = 2L, questionText = "Q2")
        val q3 = Question(id = 3L, questionText = "Q3")

        val questions = listOf(
            RevisionQuestion.Main(q1),
            RevisionQuestion.Main(q2),
            RevisionQuestion.Main(q3)
        )

        val seen = listOf(
            QuestionAnnotation(questionId = 1L, timesSeen = 10, timesCorrect = 8), // 80%
            QuestionAnnotation(questionId = 2L, timesSeen = 10, timesCorrect = 2), // 20%
            QuestionAnnotation(questionId = 3L, timesSeen = 10, timesCorrect = 5)  // 50%
        )

        every { repository.getQuestions(any(), any()) } returns flowOf(Response.Success(questions))
        every { scoreManager.getScore() } returns Score(0, 0, null, null, seen)

        val result = useCase(QuizMode.MainMode, categoryId = null, criterion = RevisionCriterion.WORST, limit = 2).first()

        assertTrue(result is Response.Success)
        val data = (result as Response.Success).data
        assertEquals(2, data.size)
        assertEquals(2L, data[0].id)
        assertEquals(3L, data[1].id)
    }
}

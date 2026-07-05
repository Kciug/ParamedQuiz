package com.rafalskrzypczyk.revisions.domain.engine

import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RevisionsSessionEngineTest {

    private lateinit var engine: RevisionsSessionEngine

    @Before
    fun setup() {
        engine = RevisionsSessionEngine()
    }

    @Test
    fun `should complete session when all questions answered correctly`() {
        val q1 = RevisionQuestion.Main(Question(id = 1L))
        val q2 = RevisionQuestion.Main(Question(id = 2L))
        engine.startSession(listOf(q1, q2))

        assertEquals(2, engine.getInitialSize())
        assertEquals(2, engine.getCurrentQueueSize())
        assertEquals(q1, engine.getCurrentQuestion())

        val res1 = engine.submitAnswer(answeredCorrectly = true)!!
        assertTrue(res1 is RevisionAnswerResult.Correct)
        assertTrue(res1.isFirstAttempt)
        assertFalse(res1.isSessionFinished)
        assertEquals(q2, engine.getCurrentQuestion())

        val res2 = engine.submitAnswer(answeredCorrectly = true)!!
        assertTrue(res2 is RevisionAnswerResult.Correct)
        assertTrue(res2.isFirstAttempt)
        assertTrue(res2.isSessionFinished)
        assertNull(engine.getCurrentQuestion())
    }

    @Test
    fun `should move incorrect question to the end of the queue`() {
        val q1 = RevisionQuestion.Main(Question(id = 1L))
        val q2 = RevisionQuestion.Main(Question(id = 2L))
        engine.startSession(listOf(q1, q2))

        val res1 = engine.submitAnswer(answeredCorrectly = false)!!
        assertTrue(res1 is RevisionAnswerResult.IncorrectQueued)
        assertTrue(res1.isFirstAttempt)
        assertFalse(res1.isSessionFinished)

        assertEquals(q2, engine.getCurrentQuestion())
        engine.submitAnswer(answeredCorrectly = true) // answer Q2 correctly

        assertEquals(q1, engine.getCurrentQuestion())
        val res1Retry = engine.submitAnswer(answeredCorrectly = true)!! // now answer Q1 correctly
        assertTrue(res1Retry is RevisionAnswerResult.Correct)
        assertFalse(res1Retry.isFirstAttempt) // first attempt was already recorded
        assertTrue(res1Retry.isSessionFinished)
    }

    @Test
    fun `should discard question after 3 errors`() {
        val q1 = RevisionQuestion.Main(Question(id = 1L))
        engine.startSession(listOf(q1))

        val res1 = engine.submitAnswer(answeredCorrectly = false)!!
        assertTrue(res1 is RevisionAnswerResult.IncorrectQueued)

        val res2 = engine.submitAnswer(answeredCorrectly = false)!!
        assertTrue(res2 is RevisionAnswerResult.IncorrectQueued)

        val res3 = engine.submitAnswer(answeredCorrectly = false)!!
        assertTrue(res3 is RevisionAnswerResult.HitErrorLimit)
        assertTrue(res3.isSessionFinished)
        assertTrue(engine.getFailedQuestionIds().contains(1L))
    }


    @Test
    fun `should calculate statistics correctly`() {
        val q1 = RevisionQuestion.Main(Question(id = 1L))
        val q2 = RevisionQuestion.Main(Question(id = 2L))
        engine.startSession(listOf(q1, q2))

        engine.submitAnswer(answeredCorrectly = false) // fail Q1 (first try)
        engine.submitAnswer(answeredCorrectly = true)  // pass Q2 (first try)
        engine.submitAnswer(answeredCorrectly = true)  // pass Q1 (second try)

        assertEquals(1, engine.getFirstInteractionCorrectCount()) // only Q2 was correct on 1st try
        assertEquals(2, engine.getCorrectAnswersCount()) // both eventually answered correctly
    }
}

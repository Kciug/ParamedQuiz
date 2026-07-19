package com.rafalskrzypczyk.revisions.presentation.config

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.domain.models.Answer
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.revisions.domain.RevisionsRepository
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.revisions.domain.use_cases.GetRevisionsQuestionsUC
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class RevisionsConfigVMTest {

    private lateinit var repository: RevisionsRepository
    private lateinit var getRevisionsQuestions: GetRevisionsQuestionsUC
    private lateinit var scoreManager: ScoreManager

    private val category = RevisionCategory(
        id = 1L,
        title = "Kategoria",
        mode = QuizMode.MainMode,
        totalQuestionsCount = 200,
        answeredQuestionsCount = 120,
        isEligible = true
    )

    @Before
    fun setup() {
        // StandardTestDispatcher, nie Unconfined - wyscig nie odtworzy sie przy zachlannym wykonaniu.
        Dispatchers.setMain(StandardTestDispatcher())

        repository = mockk()
        getRevisionsQuestions = mockk()
        scoreManager = mockk(relaxed = true)

        every { scoreManager.getScore() } returns Score.empty()
        every { repository.getCategories(any()) } returns flowOf(Response.Success(listOf(category)))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = RevisionsConfigVM(
        repository = repository,
        getRevisionsQuestions = getRevisionsQuestions,
        scoreManager = scoreManager
    )

    private fun questions(count: Int): List<RevisionQuestion> = (1..count).map { index ->
        RevisionQuestion.Main(
            Question(
                id = index.toLong(),
                questionText = "Pytanie $index",
                answers = listOf(
                    Answer(id = 1L, answerText = "A1", isCorrect = true),
                    Answer(id = 2L, answerText = "A2", isCorrect = false)
                )
            )
        )
    }

    @Test
    fun `should not overwrite state with result of a superseded criterion request`() = runTest {
        // Starszy request (WORST) rozwiazuje sie pozniej niz nowszy (UNDER_50).
        every { getRevisionsQuestions(any(), any(), RevisionCriterion.WORST, null) } returns
                flow { delay(100.milliseconds); emit(Response.Success(questions(120))) }
        every { getRevisionsQuestions(any(), any(), RevisionCriterion.UNDER_50, null) } returns
                flow { delay(10.milliseconds); emit(Response.Success(questions(5))) }
        every { getRevisionsQuestions(any(), any(), RevisionCriterion.BEST, null) } returns
                flowOf(Response.Success(questions(120)))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RevisionsConfigUIEvents.SelectCriterion(RevisionCriterion.WORST))
        viewModel.onEvent(RevisionsConfigUIEvents.SelectCriterion(RevisionCriterion.UNDER_50))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(RevisionCriterion.UNDER_50, state.selectedCriterion)
        assertEquals(5, state.availableQuestionsCount)
        assertEquals(listOf(null), state.availableLimits)
    }

    @Test
    fun `should never raise questions loading flag when switching criterion`() = runTest {
        every { getRevisionsQuestions(any(), any(), any(), null) } returns
                flowOf(Response.Success(questions(120)))

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isQuestionsLoading)

        viewModel.onEvent(RevisionsConfigUIEvents.SelectCriterion(RevisionCriterion.UNDER_50))

        // Kluczowa asercja: stan tuz po kliknieciu, przed rozwiazaniem korutyny.
        // To ten jeden kadr, w ktorym migalo serduszko.
        assertFalse(viewModel.state.value.isQuestionsLoading)

        advanceUntilIdle()
        assertFalse(viewModel.state.value.isQuestionsLoading)
    }

    @Test
    fun `should clear loading state and surface error when questions request fails`() = runTest {
        every { getRevisionsQuestions(any(), any(), any(), null) } returns
                flowOf(Response.Error("Brak polaczenia"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RevisionsConfigUIEvents.SelectMode(QuizMode.CemMode))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isQuestionsLoading)
        assertNull(state.loadingMode)
        assertTrue(state.responseState is ResponseState.Error)
    }
}

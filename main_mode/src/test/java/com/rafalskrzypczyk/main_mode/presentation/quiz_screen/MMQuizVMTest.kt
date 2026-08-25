package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.report_issues.IssueReport
import com.rafalskrzypczyk.main_mode.domain.models.Answer
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz.MMQuizUseCases
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizUIEvents
import com.rafalskrzypczyk.score.domain.Score
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MMQuizVMTest {

    private lateinit var baseUseCases: BaseQuizUseCases
    private lateinit var useCases: MMQuizUseCases
    private lateinit var adHandler: QuizAdHandler
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        baseUseCases = mockk(relaxed = true)
        useCases = mockk(relaxed = true)
        adHandler = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(
            mapOf(
                "categoryId" to 1L,
                "categoryTitle" to "Kategoria"
            )
        )

        every { useCases.base } returns baseUseCases
        every { baseUseCases.getUserScore() } returns flowOf(Score.empty())
        every { useCases.getUpdatedQuestions() } returns emptyFlow()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MMQuizVM {
        val question = Question(
            id = 100L,
            questionText = "Pytanie",
            answers = listOf(
                Answer(id = 1L, answerText = "A", isCorrect = true),
                Answer(id = 2L, answerText = "B", isCorrect = false)
            )
        )
        every { useCases.getQuestionsForCategory(1L) } returns flowOf(Response.Success(listOf(question)))

        return MMQuizVM(
            savedStateHandle = savedStateHandle,
            useCases = useCases,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager
        )
    }

    @Test
    fun `single correct answer question still allows selecting many answers`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(1L))
        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(2L))

        val selectedIds = viewModel.state.value.question.answers.filter { it.isSelected }.map { it.id }
        assertEquals(listOf(1L, 2L), selectedIds)
    }

    @Test
    fun `reported issue carries main mode game mode`() = runTest {
        val reportSlot = slot<IssueReport>()
        every { baseUseCases.reportIssue(capture(reportSlot)) } returns flowOf(Response.Success(Unit))

        val viewModel = createViewModel()
        viewModel.onEvent(MMQuizUIEvents.OnReportIssue)

        assertEquals("Main Mode", reportSlot.captured.gameMode)
    }
}

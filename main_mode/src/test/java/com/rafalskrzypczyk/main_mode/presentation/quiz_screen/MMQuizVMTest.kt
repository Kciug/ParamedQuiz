package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.QuizCompletion
import com.rafalskrzypczyk.core.analytics.QuizSource
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MMQuizVMTest {

    private lateinit var baseUseCases: BaseQuizUseCases
    private lateinit var useCases: MMQuizUseCases
    private lateinit var adHandler: QuizAdHandler
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var analyticsLogger: RecordingAnalyticsLogger

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        baseUseCases = mockk(relaxed = true)
        useCases = mockk(relaxed = true)
        adHandler = mockk(relaxed = true)
        analyticsLogger = RecordingAnalyticsLogger()
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
            feedbackManager = NoOpFeedbackManager,
            analyticsLogger = analyticsLogger
        )
    }

    @Test
    fun `leaving while the questions are still loading closes nothing`() = runTest {
        // Back dziala juz nad spinnerem, a indeks silnika jest wtedy zerowy — bez bramki
        // startu polecialby quiz_finished bez pasujacego quiz_started.
        every { useCases.getQuestionsForCategory(1L) } returns flowOf(Response.Loading)

        val viewModel = MMQuizVM(
            savedStateHandle = savedStateHandle,
            useCases = useCases,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager,
            analyticsLogger = analyticsLogger
        )

        viewModel.onEvent(MMQuizUIEvents.OnBackPressed)
        viewModel.onEvent(MMQuizUIEvents.OnBackConfirmed {})

        assertEquals(0, analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().size)
        assertEquals(0, analyticsLogger.eventsOfType<AnalyticsEvent.QuizFinished>().size)
    }

    @Test
    fun `quiz start is reported once with mode and source`() = runTest {
        createViewModel()

        val started = analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().single()
        assertEquals("main", started.mode)
        assertEquals(QuizSource.CATEGORY, started.source)
        assertEquals(1, started.questionsCount)
    }

    @Test
    fun `repeated question emissions do not duplicate the start event`() = runTest {
        val question = Question(
            id = 100L,
            questionText = "Pytanie",
            answers = listOf(
                Answer(id = 1L, answerText = "A", isCorrect = true),
                Answer(id = 2L, answerText = "B", isCorrect = false)
            )
        )
        every { useCases.getQuestionsForCategory(1L) } returns flowOf(
            Response.Success(listOf(question)),
            Response.Success(listOf(question))
        )

        MMQuizVM(
            savedStateHandle = savedStateHandle,
            useCases = useCases,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager,
            analyticsLogger = analyticsLogger
        )

        assertEquals(1, analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().size)
    }

    @Test
    fun `finished session is reported before the ad gate`() = runTest {
        every { adHandler.shouldShowAd(any(), any(), any()) } returns true
        val viewModel = createViewModel()

        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(1L))
        viewModel.onEvent(MMQuizUIEvents.OnSubmitAnswer)
        viewModel.onEvent(MMQuizUIEvents.OnNextQuestion)

        val finished = analyticsLogger.eventsOfType<AnalyticsEvent.QuizFinished>().single()
        assertEquals(QuizCompletion.COMPLETED, finished.completion)
        assertEquals(1, finished.questionsAnswered)
        // Reklama dopiero sie pokazuje, a zdarzenie juz poleci - inaczej przepadaloby przy
        // ubiciu aplikacji w trakcie interstitiala, a jej czas wszedlby w duration_sec.
        assertTrue(viewModel.state.value.showAd)
    }

    @Test
    fun `leaving before the first answer still closes the funnel`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(MMQuizUIEvents.OnBackPressed)
        viewModel.onEvent(MMQuizUIEvents.OnBackConfirmed {})

        val finished = analyticsLogger.eventsOfType<AnalyticsEvent.QuizFinished>().single()
        assertEquals(QuizCompletion.EARLY_EXIT, finished.completion)
        assertEquals(0, finished.questionsAnswered)
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

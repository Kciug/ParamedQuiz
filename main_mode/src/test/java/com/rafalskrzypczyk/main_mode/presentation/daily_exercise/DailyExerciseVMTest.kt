package com.rafalskrzypczyk.main_mode.presentation.daily_exercise

import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.report_issues.IssueReport
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.main_mode.domain.daily_exercise.DailyExerciseUseCases
import com.rafalskrzypczyk.main_mode.domain.models.Answer
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizUIEvents
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
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
class DailyExerciseVMTest {

    private lateinit var baseUseCases: BaseQuizUseCases
    private lateinit var useCases: DailyExerciseUseCases
    private lateinit var resourceProvider: ResourceProvider
    private lateinit var scoreManager: ScoreManager
    private lateinit var gameplayConfig: GameplayConfigProvider
    private lateinit var adHandler: QuizAdHandler

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        baseUseCases = mockk(relaxed = true)
        useCases = mockk(relaxed = true)
        resourceProvider = mockk(relaxed = true)
        scoreManager = mockk(relaxed = true)
        gameplayConfig = mockk(relaxed = true)
        adHandler = mockk(relaxed = true)

        val question = Question(
            id = 100L,
            questionText = "Pytanie",
            answers = listOf(
                Answer(id = 1L, answerText = "A", isCorrect = true),
                Answer(id = 2L, answerText = "B", isCorrect = false)
            )
        )

        every { useCases.base } returns baseUseCases
        every { baseUseCases.getUserScore() } returns flowOf(Score.empty())
        every { useCases.getQuestions() } returns flowOf(Response.Success(listOf(question)))
        every { useCases.getUpdatedQuestions() } returns emptyFlow()
        every { gameplayConfig.dailyExerciseQuestionsAmount() } returns 10
        every { resourceProvider.getString(any()) } returns "Ćwiczenie dnia"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = DailyExerciseVM(
        useCases = useCases,
        resourceProvider = resourceProvider,
        scoreManager = scoreManager,
        gameplayConfig = gameplayConfig,
        adHandler = adHandler,
        feedbackManager = NoOpFeedbackManager
    )

    @Test
    fun `reported issue carries daily exercise game mode`() = runTest {
        val reportSlot = slot<IssueReport>()
        every { baseUseCases.reportIssue(capture(reportSlot)) } returns flowOf(Response.Success(Unit))

        val viewModel = createViewModel()
        viewModel.onEvent(MMQuizUIEvents.OnReportIssue)

        assertEquals("Daily Exercise", reportSlot.captured.gameMode)
        assertEquals(100L, reportSlot.captured.questionId)
    }

    @Test
    fun `single correct answer question still allows selecting many answers`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(1L))
        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(2L))

        val selectedIds = viewModel.state.value.question.answers.filter { it.isSelected }.map { it.id }
        assertEquals(listOf(1L, 2L), selectedIds)
        assertTrue(viewModel.state.value.isDailyExercise)
    }
}

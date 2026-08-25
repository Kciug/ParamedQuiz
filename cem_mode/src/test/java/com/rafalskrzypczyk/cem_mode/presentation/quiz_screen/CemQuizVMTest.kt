package com.rafalskrzypczyk.cem_mode.presentation.quiz_screen

import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.cem_mode.domain.use_cases.CemQuestionsUseCases
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.main_mode.domain.models.Answer
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizUIEvents
import com.rafalskrzypczyk.score.domain.Score
import io.mockk.every
import io.mockk.mockk
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
class CemQuizVMTest {

    private lateinit var baseUseCases: BaseQuizUseCases
    private lateinit var useCases: CemQuestionsUseCases
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

    private fun createViewModel(question: Question): CemQuizVM {
        every { useCases.getQuestionsForCategory(1L) } returns flowOf(Response.Success(listOf(question)))

        return CemQuizVM(
            savedStateHandle = savedStateHandle,
            useCases = useCases,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager
        )
    }

    private fun questionWith(vararg correctAnswerIds: Long) = Question(
        id = 100L,
        questionText = "Pytanie",
        answers = listOf(
            Answer(id = 1L, answerText = "A", isCorrect = 1L in correctAnswerIds),
            Answer(id = 2L, answerText = "B", isCorrect = 2L in correctAnswerIds),
            Answer(id = 3L, answerText = "C", isCorrect = 3L in correctAnswerIds)
        )
    )

    private fun CemQuizVM.selectedIds() = state.value.question.answers.filter { it.isSelected }.map { it.id }

    @Test
    fun `single correct answer question keeps only one answer selected`() = runTest {
        val viewModel = createViewModel(questionWith(1L))

        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(1L))
        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(2L))

        assertEquals(listOf(2L), viewModel.selectedIds())
    }

    @Test
    fun `single correct answer question deselects answer on second click`() = runTest {
        val viewModel = createViewModel(questionWith(1L))

        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(3L))
        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(3L))

        assertEquals(emptyList<Long>(), viewModel.selectedIds())
    }

    @Test
    fun `multiple correct answers question allows selecting many answers`() = runTest {
        val viewModel = createViewModel(questionWith(1L, 2L))

        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(1L))
        viewModel.onEvent(MMQuizUIEvents.OnAnswerClicked(3L))

        assertEquals(listOf(1L, 3L), viewModel.selectedIds())
    }
}

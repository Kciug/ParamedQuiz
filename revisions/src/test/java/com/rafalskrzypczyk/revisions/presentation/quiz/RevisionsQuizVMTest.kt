package com.rafalskrzypczyk.revisions.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.firestore.domain.use_cases.ReportIssueUC
import com.rafalskrzypczyk.main_mode.domain.models.Answer
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.revisions.domain.use_cases.GetRevisionsQuestionsUC
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.StreakManager
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class RevisionsQuizVMTest {

    private lateinit var getRevisionsQuestions: GetRevisionsQuestionsUC
    private lateinit var updateScoreWithQuestion: UpdateScoreWithQuestionUC
    private lateinit var scoreManager: ScoreManager
    private lateinit var streakManager: StreakManager
    private lateinit var reportIssueUC: ReportIssueUC
    private lateinit var adHandler: QuizAdHandler
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: RevisionsQuizVM

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        getRevisionsQuestions = mockk()
        updateScoreWithQuestion = mockk(relaxed = true)
        scoreManager = mockk(relaxed = true)
        streakManager = mockk(relaxed = true)
        reportIssueUC = mockk(relaxed = true)
        adHandler = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(
            mapOf(
                "mode" to QuizMode.MainMode.name,
                "categoryId" to 1L,
                "criterion" to RevisionCriterion.WORST.name,
                "limit" to 10
            )
        )

        every { scoreManager.getScore() } returns Score.empty()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load questions and initialize session successfully`() = runTest {
        val q1 = Question(
            id = 101L,
            questionText = "Question 1",
            answers = listOf(
                Answer(id = 1L, answerText = "A1", isCorrect = true),
                Answer(id = 2L, answerText = "A2", isCorrect = false)
            )
        )
        val questions = listOf(RevisionQuestion.Main(q1))

        every { getRevisionsQuestions(any(), any(), any(), any()) } returns flowOf(Response.Success(questions))

        viewModel = RevisionsQuizVM(
            savedStateHandle = savedStateHandle,
            getRevisionsQuestions = getRevisionsQuestions,
            updateScoreWithQuestion = updateScoreWithQuestion,
            scoreManager = scoreManager,
            streakManager = streakManager,
            reportIssueUC = reportIssueUC,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager
        )

        val state = viewModel.state.value
        assertEquals(ResponseState.Success, state.responseState)
        assertEquals(1, state.originalQuestions.size)
        assertEquals(101L, state.currentQuestionUIM?.id)
    }

    @Test
    fun `should submit correct answer and update points and streak on first attempt`() = runTest {
        val q1 = Question(
            id = 101L,
            questionText = "Question 1",
            answers = listOf(
                Answer(id = 1L, answerText = "A1", isCorrect = true),
                Answer(id = 2L, answerText = "A2", isCorrect = false)
            )
        )
        val questions = listOf(RevisionQuestion.Main(q1))

        every { getRevisionsQuestions(any(), any(), any(), any()) } returns flowOf(Response.Success(questions))
        every { updateScoreWithQuestion(101L, true) } returns 10

        viewModel = RevisionsQuizVM(
            savedStateHandle = savedStateHandle,
            getRevisionsQuestions = getRevisionsQuestions,
            updateScoreWithQuestion = updateScoreWithQuestion,
            scoreManager = scoreManager,
            streakManager = streakManager,
            reportIssueUC = reportIssueUC,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager
        )

        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(1L))
        viewModel.onEvent(RevisionsQuizUIEvents.OnSubmitAnswer)

        val stateAfterSubmit = viewModel.state.value
        assertTrue(stateAfterSubmit.currentQuestionUIM?.isAnswerSubmitted == true)
        assertTrue(stateAfterSubmit.currentQuestionUIM?.isAnswerCorrect == true)

        viewModel.onEvent(RevisionsQuizUIEvents.OnNextQuestion)

        val stateAfterNext = viewModel.state.value
        assertTrue(stateAfterNext.quizFinished)
        assertEquals(10, stateAfterNext.quizFinishedState.earnedPoints)
        coVerify(exactly = 1) { streakManager.increaseStreak() }
    }

    @Test
    fun `should queue incorrect answer and not update streak`() = runTest {
        val q1 = Question(
            id = 101L,
            questionText = "Question 1",
            answers = listOf(
                Answer(id = 1L, answerText = "A1", isCorrect = true),
                Answer(id = 2L, answerText = "A2", isCorrect = false)
            )
        )
        val questions = listOf(RevisionQuestion.Main(q1))

        every { getRevisionsQuestions(any(), any(), any(), any()) } returns flowOf(Response.Success(questions))

        viewModel = RevisionsQuizVM(
            savedStateHandle = savedStateHandle,
            getRevisionsQuestions = getRevisionsQuestions,
            updateScoreWithQuestion = updateScoreWithQuestion,
            scoreManager = scoreManager,
            streakManager = streakManager,
            reportIssueUC = reportIssueUC,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager
        )

        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(2L))
        viewModel.onEvent(RevisionsQuizUIEvents.OnSubmitAnswer)

        val stateAfterSubmit = viewModel.state.value
        assertTrue(stateAfterSubmit.currentQuestionUIM?.isAnswerSubmitted == true)
        assertTrue(stateAfterSubmit.currentQuestionUIM?.isAnswerCorrect == false)

        viewModel.onEvent(RevisionsQuizUIEvents.OnNextQuestion)

        val stateAfterNext = viewModel.state.value
        assertTrue(!stateAfterNext.quizFinished)
        coVerify(exactly = 0) { streakManager.increaseStreak() }
    }

    @Test
    fun `should support translation questions correctly`() = runTest {
        val q1 = TranslationQuestionDTO(
            id = 201L,
            phrase = "Apple",
            translations = listOf("Jabłko")
        )
        val questions = listOf(RevisionQuestion.Translation(q1))

        val translationSavedStateHandle = SavedStateHandle(
            mapOf(
                "mode" to QuizMode.TranslationMode.name,
                "categoryId" to null,
                "criterion" to RevisionCriterion.WORST.name,
                "limit" to 5
            )
        )

        every { getRevisionsQuestions(any(), any(), any(), any()) } returns flowOf(Response.Success(questions))
        every { updateScoreWithQuestion(201L, true) } returns 10

        viewModel = RevisionsQuizVM(
            savedStateHandle = translationSavedStateHandle,
            getRevisionsQuestions = getRevisionsQuestions,
            updateScoreWithQuestion = updateScoreWithQuestion,
            scoreManager = scoreManager,
            streakManager = streakManager,
            reportIssueUC = reportIssueUC,
            adHandler = adHandler,
            feedbackManager = NoOpFeedbackManager
        )

        viewModel.onEvent(RevisionsQuizUIEvents.OnTranslationAnswerChanged("Jabłko"))
        viewModel.onEvent(RevisionsQuizUIEvents.OnSubmitAnswer)

        val stateAfterSubmit = viewModel.state.value
        assertTrue(stateAfterSubmit.currentTranslationQuestionUIM?.isAnswered == true)
        assertTrue(stateAfterSubmit.currentTranslationQuestionUIM?.isCorrect == true)

        viewModel.onEvent(RevisionsQuizUIEvents.OnNextQuestion)

        val stateAfterNext = viewModel.state.value
        assertTrue(stateAfterNext.quizFinished)
        assertEquals(10, stateAfterNext.quizFinishedState.earnedPoints)
        coVerify(exactly = 1) { streakManager.increaseStreak() }
    }

    @Test
    fun `should exclude corrected answers from first attempt accuracy`() = runTest {
        val questions = listOf(
            RevisionQuestion.Main(question(101L)),
            RevisionQuestion.Main(question(102L))
        )

        every { getRevisionsQuestions(any(), any(), any(), any()) } returns flowOf(Response.Success(questions))
        every { updateScoreWithQuestion(any(), any()) } returns 10

        viewModel = createViewModel()

        // q1 blednie - wraca na koniec kolejki, wiec kolejne jest q2.
        answerCurrentQuestion(correct = false)
        // q2 poprawnie za pierwszym razem.
        answerCurrentQuestion(correct = true)
        // q1 poprawnie, ale dopiero w korekcie - to nie moze podniesc skutecznosci.
        answerCurrentQuestion(correct = true)

        val state = viewModel.state.value
        assertTrue(state.quizFinished)
        assertEquals("Oba pytania zaliczone lacznie", 2, state.quizFinishedState.correctAnswers)
        assertEquals("Tylko q2 za pierwszym podejsciem", 1, state.firstAttemptCorrectCount)
        assertEquals("1 z 2 podjetych pytan", 50, state.firstAttemptAccuracy)
    }

    @Test
    fun `should count only attempted questions when session is exited early`() = runTest {
        val questions = listOf(
            RevisionQuestion.Main(question(101L)),
            RevisionQuestion.Main(question(102L)),
            RevisionQuestion.Main(question(103L))
        )

        every { getRevisionsQuestions(any(), any(), any(), any()) } returns flowOf(Response.Success(questions))
        every { updateScoreWithQuestion(any(), any()) } returns 10

        viewModel = createViewModel()

        answerCurrentQuestion(correct = true)

        viewModel.onEvent(RevisionsQuizUIEvents.OnBackPressed)
        viewModel.onEvent(RevisionsQuizUIEvents.OnBackConfirmed)

        val state = viewModel.state.value
        assertTrue(state.quizFinished)
        assertEquals("Podsumowanie liczy podjete pytania, nie cala pule", 1, state.quizFinishedState.seenQuestions)
        assertEquals(1, state.quizFinishedState.correctAnswers)
        assertEquals(100, state.firstAttemptAccuracy)
    }

    private fun question(id: Long) = Question(
        id = id,
        questionText = "Question $id",
        answers = listOf(
            Answer(id = CORRECT_ANSWER_ID, answerText = "A1", isCorrect = true),
            Answer(id = WRONG_ANSWER_ID, answerText = "A2", isCorrect = false)
        )
    )

    @Test
    fun `cem session keeps only one answer selected for single correct answer question`() = runTest {
        val question = singleCorrectAnswerQuestion()
        every { getRevisionsQuestions(any(), any(), any(), any()) } returns
            flowOf(Response.Success(listOf(RevisionQuestion.Cem(question))))

        viewModel = createViewModel(cemSavedStateHandle())

        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(CORRECT_ANSWER_ID))
        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(WRONG_ANSWER_ID))

        assertEquals(listOf(WRONG_ANSWER_ID), viewModel.selectedAnswerIds())
    }

    @Test
    fun `cem session allows many selected answers for multiple correct answers question`() = runTest {
        val question = multipleCorrectAnswersQuestion()
        every { getRevisionsQuestions(any(), any(), any(), any()) } returns
            flowOf(Response.Success(listOf(RevisionQuestion.Cem(question))))

        viewModel = createViewModel(cemSavedStateHandle())

        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(CORRECT_ANSWER_ID))
        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(WRONG_ANSWER_ID))

        assertEquals(listOf(CORRECT_ANSWER_ID, WRONG_ANSWER_ID), viewModel.selectedAnswerIds())
    }

    @Test
    fun `main mode session allows many selected answers for single correct answer question`() = runTest {
        val question = singleCorrectAnswerQuestion()
        every { getRevisionsQuestions(any(), any(), any(), any()) } returns
            flowOf(Response.Success(listOf(RevisionQuestion.Main(question))))

        viewModel = createViewModel()

        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(CORRECT_ANSWER_ID))
        viewModel.onEvent(RevisionsQuizUIEvents.OnAnswerSelected(WRONG_ANSWER_ID))

        assertEquals(listOf(CORRECT_ANSWER_ID, WRONG_ANSWER_ID), viewModel.selectedAnswerIds())
    }

    private fun cemSavedStateHandle() = SavedStateHandle(
        mapOf(
            "mode" to QuizMode.CemMode.name,
            "categoryId" to 1L,
            "criterion" to RevisionCriterion.WORST.name,
            "limit" to 10
        )
    )

    private fun singleCorrectAnswerQuestion() = Question(
        id = 301L,
        questionText = "Pytanie jednokrotnego wyboru",
        answers = listOf(
            Answer(id = CORRECT_ANSWER_ID, answerText = "A1", isCorrect = true),
            Answer(id = WRONG_ANSWER_ID, answerText = "A2", isCorrect = false)
        )
    )

    private fun multipleCorrectAnswersQuestion() = Question(
        id = 302L,
        questionText = "Pytanie wielokrotnego wyboru",
        answers = listOf(
            Answer(id = CORRECT_ANSWER_ID, answerText = "A1", isCorrect = true),
            Answer(id = WRONG_ANSWER_ID, answerText = "A2", isCorrect = true)
        )
    )

    private fun RevisionsQuizVM.selectedAnswerIds() =
        state.value.currentQuestionUIM?.answers.orEmpty().filter { it.isSelected }.map { it.id }

    private fun createViewModel(handle: SavedStateHandle = savedStateHandle) = RevisionsQuizVM(
        savedStateHandle = handle,
        getRevisionsQuestions = getRevisionsQuestions,
        updateScoreWithQuestion = updateScoreWithQuestion,
        scoreManager = scoreManager,
        streakManager = streakManager,
        reportIssueUC = reportIssueUC,
        adHandler = adHandler,
        feedbackManager = NoOpFeedbackManager
    )

    /** Zatwierdza odpowiedz na biezace pytanie i przechodzi dalej. */
    private fun answerCurrentQuestion(correct: Boolean) {
        viewModel.onEvent(
            RevisionsQuizUIEvents.OnAnswerSelected(if (correct) CORRECT_ANSWER_ID else WRONG_ANSWER_ID)
        )
        viewModel.onEvent(RevisionsQuizUIEvents.OnSubmitAnswer)
        viewModel.onEvent(RevisionsQuizUIEvents.OnNextQuestion)
    }

    private companion object {
        const val CORRECT_ANSWER_ID = 1L
        const val WRONG_ANSWER_ID = 2L
    }
}

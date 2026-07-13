package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.firestore.domain.models.AnswerDTO
import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO
import com.rafalskrzypczyk.main_mode.domain.quiz.MMQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizScreen
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.MMQuizVM
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

/**
 * E2E-MAIN-01: rozegranie quizu kategorii do ekranu wyniku.
 *
 * Napędza realny [MMQuizScreen] + [MMQuizVM] (silnik quizu + use case'y) na zaseedowanych pytaniach
 * z [FakeFirestoreApi] — bez Firebase. Klika poprawne odpowiedzi, zatwierdza i przechodzi dalej,
 * aż do ekranu wyniku; weryfikuje też liczbę poprawnych w stanie końcowym.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class MainModeQuizHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var fakeFirestore: FakeFirestoreApi

    @Inject
    lateinit var useCases: MMQuizUseCases

    @Inject
    lateinit var adHandler: QuizAdHandler

    private val categoryId = 100L
    private val viewModelStore = ViewModelStore()

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeFirestore.quizQuestions = listOf(
            QuestionDTO(
                id = 1,
                questionText = "PYTANIE_1",
                categoryIDs = listOf(categoryId),
                answers = listOf(
                    AnswerDTO(id = 1, answerText = "ODP_1A", isCorrect = true),
                    AnswerDTO(id = 2, answerText = "ODP_1B", isCorrect = false)
                )
            ),
            QuestionDTO(
                id = 2,
                questionText = "PYTANIE_2",
                categoryIDs = listOf(categoryId),
                answers = listOf(
                    AnswerDTO(id = 3, answerText = "ODP_2A", isCorrect = true),
                    AnswerDTO(id = 4, answerText = "ODP_2B", isCorrect = false)
                )
            )
        )
    }

    @Test
    fun `play category quiz through to finish screen`() {
        val viewModel = MMQuizVM(
            SavedStateHandle(mapOf("categoryId" to categoryId, "categoryTitle" to "KATEGORIA")),
            useCases,
            adHandler
        ).also { viewModelStore.put("vm", it) }

        composeRule.setContent {
            val state by viewModel.state.collectAsState()
            ParamedQuizTheme {
                MMQuizScreen(
                    state = state,
                    effect = viewModel.effect,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = {}
                )
            }
        }

        answerCorrectlyAndAdvance("ODP_1A")
        answerCorrectlyAndAdvance("ODP_2A")

        // Ekran wyniku osiągnięty…
        waitForTag(TestTags.QUIZ_FINISHED_ROOT)
        composeRule.onNodeWithTag(TestTags.QUIZ_FINISHED_ROOT).assertExists()

        // …i obie odpowiedzi policzone jako poprawne.
        assertEquals(2, viewModel.state.value.quizFinishedState.correctAnswers)
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    private fun answerCorrectlyAndAdvance(correctAnswerText: String) {
        waitForText(correctAnswerText)
        composeRule.onNodeWithText(correctAnswerText).performClick()
        composeRule.onNodeWithTag(TestTags.QUIZ_SUBMIT_BUTTON).performClick()
        waitForTag(TestTags.QUIZ_NEXT_BUTTON)
        composeRule.onNodeWithTag(TestTags.QUIZ_NEXT_BUTTON).performClick()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }
}

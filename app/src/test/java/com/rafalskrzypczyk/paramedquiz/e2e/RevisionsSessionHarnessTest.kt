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
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.firestore.domain.models.AnswerDTO
import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO
import com.rafalskrzypczyk.firestore.domain.use_cases.ReportIssueUC
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import com.rafalskrzypczyk.revisions.domain.use_cases.GetRevisionsQuestionsUC
import com.rafalskrzypczyk.revisions.presentation.quiz.RevisionsQuizScreen
import com.rafalskrzypczyk.revisions.presentation.quiz.RevisionsQuizVM
import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.ScoreStorage
import com.rafalskrzypczyk.score.domain.StreakManager
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

/**
 * E2E-REV-01: konfiguracja powtórek → sesja.
 *
 * Powtórki działają wyłącznie na pytaniach już odpowiadanych (historia `seenQuestions`). Test seeduje
 * pytanie + historię (kryterium „najsłabsze"), buduje sesję powtórki (tryb główny, WORST, bez limitu)
 * i rozgrywa ją do ekranu wyniku. Reużywa test-tagów quizu (submit/next/finish).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class RevisionsSessionHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var fakeFirestore: FakeFirestoreApi

    @Inject
    lateinit var getRevisionsQuestions: GetRevisionsQuestionsUC

    @Inject
    lateinit var updateScoreWithQuestion: UpdateScoreWithQuestionUC

    @Inject
    lateinit var scoreManager: ScoreManager

    @Inject
    lateinit var scoreStorage: ScoreStorage

    @Inject
    lateinit var streakManager: StreakManager

    @Inject
    lateinit var reportIssueUC: ReportIssueUC

    @Inject
    lateinit var adHandler: QuizAdHandler

    private val viewModelStore = ViewModelStore()
    private val questionId = 1L

    private val seededScore = Score.empty().copy(
        seenQuestions = listOf(
            QuestionAnnotation(questionId = questionId, timesSeen = 2, timesCorrect = 0)
        )
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        // Seed magazynu — początkowy async fetch ScoreManagera zwróci historię (a nie pusty stan),
        // więc nie nadpisze seedu (obrona przed wyścigiem).
        scoreStorage.saveScore(seededScore)
        fakeFirestore.quizQuestions = listOf(
            QuestionDTO(
                id = questionId,
                questionText = "PYTANIE_POWTORKA",
                categoryIDs = listOf(100),
                answers = listOf(
                    AnswerDTO(id = 1, answerText = "ODP_POPRAWNA", isCorrect = true),
                    AnswerDTO(id = 2, answerText = "ODP_BLEDNA", isCorrect = false)
                )
            )
        )
    }

    @Test
    fun `revision session loads eligible questions and plays to finish`() {
        // Pytanie „widziane" (historia) → kwalifikuje się do powtórki (kryterium najsłabsze).
        scoreManager.updateScore(seededScore)

        val viewModel = RevisionsQuizVM(
            SavedStateHandle(mapOf("mode" to "MainMode", "criterion" to "WORST")),
            getRevisionsQuestions,
            updateScoreWithQuestion,
            scoreManager,
            streakManager,
            reportIssueUC,
            adHandler,
            NoOpFeedbackManager
        ).also { viewModelStore.put("vm", it) }

        composeRule.setContent {
            val state by viewModel.state.collectAsState()
            ParamedQuizTheme {
                RevisionsQuizScreen(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = {}
                )
            }
        }

        // Sesja załadowała zakwalifikowane pytanie → rozgrywka do ekranu wyniku.
        waitForText("ODP_POPRAWNA")
        composeRule.onNodeWithText("ODP_POPRAWNA").performClick()
        composeRule.onNodeWithTag(TestTags.QUIZ_SUBMIT_BUTTON).performClick()
        waitForTag(TestTags.QUIZ_NEXT_BUTTON)
        composeRule.onNodeWithTag(TestTags.QUIZ_NEXT_BUTTON).performClick()
        waitForTag(TestTags.QUIZ_FINISHED_ROOT)

        assertTrue("Sesja powtórki powinna się zakończyć", viewModel.state.value.quizFinished)
        assertEquals("Jedna poprawna odpowiedź", 1, viewModel.state.value.quizFinishedState.correctAnswers)
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 30_000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }
}

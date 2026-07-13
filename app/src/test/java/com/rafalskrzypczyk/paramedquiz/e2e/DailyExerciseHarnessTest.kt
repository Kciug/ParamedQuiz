package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.domain.models.AnswerDTO
import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO
import com.rafalskrzypczyk.home_screen.domain.CheckDailyExerciseAvailabilityUC
import com.rafalskrzypczyk.main_mode.domain.daily_exercise.DailyExerciseUseCases
import com.rafalskrzypczyk.main_mode.presentation.daily_exercise.DailyExerciseVM
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizScreen
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeTimeProvider
import androidx.lifecycle.ViewModelStore
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Calendar
import javax.inject.Inject

/**
 * E2E-DAILY-01: ćwiczenie dnia → podbicie serii → niedostępne drugi raz tego samego dnia.
 *
 * Czas jest ustalony przez [FakeTimeProvider], więc test jest deterministyczny (niezależny od
 * rzeczywistej daty / północy). Reużywa wzorca quizu (MMQuizScreen + BaseQuizVM).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class DailyExerciseHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var fakeFirestore: FakeFirestoreApi

    @Inject
    lateinit var fakeTime: FakeTimeProvider

    @Inject
    lateinit var dailyUseCases: DailyExerciseUseCases

    @Inject
    lateinit var resourceProvider: ResourceProvider

    @Inject
    lateinit var scoreManager: ScoreManager

    @Inject
    lateinit var gameplayConfig: GameplayConfigProvider

    @Inject
    lateinit var adHandler: QuizAdHandler

    @Inject
    lateinit var checkDailyAvailability: CheckDailyExerciseAvailabilityUC

    private val viewModelStore = ViewModelStore()

    @Before
    fun setUp() {
        hiltRule.inject()
        // Ustalony dzień → deterministyczne porównania dat.
        fakeTime.current = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 15, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        fakeFirestore.quizQuestions = listOf(
            QuestionDTO(
                id = 1,
                questionText = "PYTANIE_DNIA",
                categoryIDs = listOf(1),
                answers = listOf(
                    AnswerDTO(id = 1, answerText = "ODP_OK", isCorrect = true),
                    AnswerDTO(id = 2, answerText = "ODP_ZLE", isCorrect = false)
                )
            )
        )
    }

    @Test
    fun `daily exercise bumps streak and becomes unavailable same day`() {
        assertEquals("Warunek startowy: streak = 0", 0, scoreManager.getScore().streak)
        assertFalse(
            "Warunek startowy: brak zapisanej daty ćwiczenia",
            scoreManager.getScore().lastDailyExerciseDate != null
        )

        val viewModel = DailyExerciseVM(
            dailyUseCases,
            resourceProvider,
            scoreManager,
            gameplayConfig,
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

        // Rozegranie jedynego pytania → ekran wyniku (reklama końcowa obsłużona przez NoOp AdManager).
        waitForText("ODP_OK")
        composeRule.onNodeWithText("ODP_OK").performClick()
        composeRule.onNodeWithTag(TestTags.QUIZ_SUBMIT_BUTTON).performClick()
        waitForTag(TestTags.QUIZ_NEXT_BUTTON)
        composeRule.onNodeWithTag(TestTags.QUIZ_NEXT_BUTTON).performClick()
        waitForTag(TestTags.QUIZ_FINISHED_ROOT)

        // Seria podbita i data ćwiczenia zapisana na „dziś".
        assertEquals("Seria powinna wzrosnąć do 1", 1, scoreManager.getScore().streak)
        val lastDate = scoreManager.getScore().lastDailyExerciseDate
        assertFalse("Data ćwiczenia powinna być zapisana", lastDate == null)

        // Ćwiczenie niedostępne drugi raz tego samego dnia.
        assertFalse(
            "Ćwiczenie dnia powinno być niedostępne tego samego dnia",
            checkDailyAvailability(lastDate)
        )
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
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

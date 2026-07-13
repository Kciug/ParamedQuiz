package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.firestore.domain.models.SwipeQuestionDTO
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakePremiumStatusProvider
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeUseCases
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeScreen
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeUIEvents
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeVM
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertFalse
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
 * E2E-SWIPE-01: wersja próbna → wyczerpanie darmowych pytań → panel zakończenia triala → zakup →
 * odblokowanie pełnego trybu.
 *
 * Odpowiedzi napędzamy przez zdarzenia (gest przeciągnięcia karty produkuje to samo `SubmitAnswer`),
 * a nadanie uprawnienia po zakupie symulujemy przez [FakePremiumStatusProvider] — co odzwierciedla
 * „zakup potwierdzony → dostęp" (w produkcji robi to provider oparty o Billing).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class SwipeModeTrialHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var fakeFirestore: FakeFirestoreApi

    @Inject
    lateinit var useCases: SwipeModeUseCases

    @Inject
    lateinit var adHandler: QuizAdHandler

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var fakePremium: FakePremiumStatusProvider

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeFirestore.swipeQuestions = listOf(
            SwipeQuestionDTO(id = 1, text = "TEZA_TRIAL", isCorrect = true, isFree = true),
            SwipeQuestionDTO(id = 2, text = "TEZA_FULL", isCorrect = false, isFree = false)
        )
    }

    @Test
    fun `trial exhaustion shows purchase panel and purchase unlocks full mode`() {
        val viewModel = SwipeModeVM(
            useCases,
            adHandler,
            billingRepository,
            fakePremium,
            SavedStateHandle(mapOf("isTrial" to true))
        )

        composeRule.setContent {
            val state by viewModel.state.collectAsState()
            ParamedQuizTheme {
                SwipeModeScreen(
                    state = state,
                    effect = viewModel.effect,
                    quizEffect = viewModel.quizEffect,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = {},
                    onLaunchBilling = { activity -> viewModel.launchBillingFlow(activity) }
                )
            }
        }

        // 1) Wczytana wersja próbna z jednym darmowym pytaniem.
        composeRule.waitUntil(timeoutMillis = 5_000) {
            viewModel.state.value.questionsPair.isNotEmpty()
        }
        assertTrue("Sesja powinna startować jako trial", viewModel.state.value.isTrial)

        // 2) Odpowiedź na jedyne darmowe pytanie → panel zakończenia triala.
        val currentQuestionId = viewModel.state.value.questionsPair.last().id
        composeRule.runOnIdle {
            viewModel.onEvent(SwipeModeUIEvents.SubmitAnswer(currentQuestionId, isCorrect = true))
        }
        composeRule.runOnIdle {
            viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            viewModel.state.value.showTrialFinishedPanel
        }

        // 3) Cena produktu wczytana (przycisk kupna aktywny), przycisk widoczny → klik.
        composeRule.waitUntil(timeoutMillis = 5_000) {
            viewModel.state.value.swipeModePrice != null
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(TestTags.SWIPE_TRIAL_BUY_BUTTON).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag(TestTags.SWIPE_TRIAL_BUY_BUTTON).performClick()

        // 4) Nadanie uprawnienia (zakup potwierdzony) → tryb przełącza się z triala na pełny.
        composeRule.runOnIdle {
            fakePremium.setOwned(BillingIds.ID_SWIPE_MODE)
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            !viewModel.state.value.isTrial
        }

        assertFalse("Po zakupie tryb nie powinien być już trialem", viewModel.state.value.isTrial)
        assertFalse("Panel triala powinien zniknąć", viewModel.state.value.showTrialFinishedPanel)
    }
}

package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakePremiumStatusProvider
import com.rafalskrzypczyk.translation_mode.domain.use_cases.TranslationUseCases
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizEvents
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizScreen
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Assert.assertEquals
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
 * E2E-TRANSLATION-01: wersja próbna → wyczerpanie darmowych pytań → panel zakończenia triala →
 * zakup → kontynuacja quizu.
 *
 * Regresja: przed poprawką przeładowanie po zakupie budowało listę pytań od zera, przez co
 * quiz wracał do pytania 1 i gubił odpowiedzi udzielone w trialu.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class TranslationModeTrialHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var fakeFirestore: FakeFirestoreApi

    @Inject
    lateinit var useCases: TranslationUseCases

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var fakePremium: FakePremiumStatusProvider

    private val viewModelStore = ViewModelStore()

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeFirestore.translationQuestions = listOf(
            TranslationQuestionDTO(
                id = 1,
                phrase = "FRAZA_TRIAL",
                translations = listOf("trial"),
                isFree = true
            ),
            TranslationQuestionDTO(
                id = 2,
                phrase = "FRAZA_FULL",
                translations = listOf("full"),
                isFree = false
            )
        )
    }

    @Test
    fun `purchase after trial keeps progress and answers`() {
        val viewModel = TranslationQuizViewModel(
            useCases,
            billingRepository,
            fakePremium as PremiumStatusProvider,
            NoOpFeedbackManager,
            SavedStateHandle(mapOf("isTrial" to true))
        ).also { viewModelStore.put("vm", it) }

        composeRule.setContent {
            val state by viewModel.state.collectAsState()
            ParamedQuizTheme {
                TranslationQuizScreen(
                    state = state,
                    effect = viewModel.effect,
                    billingEffect = viewModel.billingEffect,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = {},
                    onLaunchBilling = { }
                )
            }
        }

        // 1) Trial wczytany - tylko pytanie oznaczone jako darmowe.
        composeRule.waitUntil(timeoutMillis = 30_000) {
            viewModel.state.value.questions.isNotEmpty()
        }
        assertTrue("Sesja powinna startować jako trial", viewModel.state.value.isTrial)
        assertEquals(
            "Trial powinien podać wyłącznie darmowe pytanie",
            listOf(1L),
            viewModel.state.value.questions.map { it.id }
        )

        // 2) Odpowiedź na jedyne darmowe pytanie → panel zakończenia triala.
        composeRule.runOnIdle {
            viewModel.onEvent(TranslationQuizEvents.OnAnswerChanged("trial"))
            viewModel.onEvent(TranslationQuizEvents.OnSubmitAnswer)
            viewModel.onEvent(TranslationQuizEvents.OnNextQuestion)
        }
        composeRule.waitUntil(timeoutMillis = 30_000) {
            viewModel.state.value.showTrialFinishedPanel
        }

        // 3) Nadanie uprawnienia (zakup potwierdzony) → pełny tryb.
        composeRule.runOnIdle {
            fakePremium.setOwned(BillingIds.ID_TRANSLATION_MODE)
        }
        composeRule.waitUntil(timeoutMillis = 30_000) {
            !viewModel.state.value.isTrial
        }
        composeRule.waitUntil(timeoutMillis = 30_000) {
            viewModel.state.value.questions.size == 2
        }

        val state = viewModel.state.value

        assertFalse("Panel triala powinien zniknąć", state.showTrialFinishedPanel)

        // 4) Odpowiedź z triala przetrwała podmianę listy.
        val trialQuestion = state.questions.first { it.id == 1L }
        assertTrue("Pytanie z triala powinno zostać odpowiedziane", trialQuestion.isAnswered)
        assertEquals("Odpowiedź użytkownika powinna się zachować", "trial", trialQuestion.userAnswer)

        // 5) Quiz nie wrócił na start - stoimy na nowo odblokowanym pytaniu.
        assertEquals(
            "Po zakupie quiz powinien przejść do pytania spoza puli darmowej",
            2L,
            state.currentQuestion?.id
        )
        assertFalse(
            "Nowe pytanie nie powinno być oznaczone jako odpowiedziane",
            state.currentQuestion?.isAnswered ?: true
        )
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
    }
}

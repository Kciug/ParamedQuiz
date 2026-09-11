package com.rafalskrzypczyk.translation_mode.presentation

import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.billing.analytics.PurchaseFunnelTracker
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.Paywall
import com.rafalskrzypczyk.core.analytics.QuizType
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.translation_mode.domain.use_cases.TranslationUseCases
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Niezmienniki analityki sesji Tlumaczen. `loadData()` leci ponownie po zakupie w trialu
 * (z zachowaniem postepu przez `mergeQuestions`), a listener Firestore potrafi ja powtorzyc —
 * jednorazowosc startu i zamrozony typ sesji sa tu jedynymi bramkami.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TranslationQuizViewModelTest {

    private lateinit var useCases: TranslationUseCases
    private lateinit var billingRepository: BillingRepository
    private lateinit var premiumStatusProvider: PremiumStatusProvider
    private lateinit var analyticsLogger: RecordingAnalyticsLogger
    private lateinit var purchaseFunnelTracker: PurchaseFunnelTracker

    private val ownedProductIds = MutableStateFlow<Set<String>>(emptySet())
    private val pendingProductIds = MutableStateFlow<Set<String>>(emptySet())

    private val trialPool = listOf(
        TranslationQuestionDTO(id = 1L, phrase = "P1", translations = listOf("one"), isFree = true),
    )
    private val fullPool = trialPool + listOf(
        TranslationQuestionDTO(id = 2L, phrase = "P2", translations = listOf("two"), isFree = false),
        TranslationQuestionDTO(id = 3L, phrase = "P3", translations = listOf("three"), isFree = false),
        TranslationQuestionDTO(id = 4L, phrase = "P4", translations = listOf("four"), isFree = false),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        useCases = mockk(relaxed = true)
        billingRepository = mockk(relaxed = true)
        premiumStatusProvider = mockk(relaxed = true)
        analyticsLogger = RecordingAnalyticsLogger()
        purchaseFunnelTracker = mockk(relaxed = true)

        every { useCases.getUserScore() } returns flowOf(Score.empty())
        every { useCases.getTranslationQuestions() } returns flowOf(Response.Success(fullPool))
        every { useCases.getTranslationTrialQuestions() } returns flowOf(Response.Success(trialPool))
        every { useCases.getUpdatedTranslationQuestions() } returns emptyFlow()
        every { useCases.getUpdatedTranslationTrialQuestions() } returns emptyFlow()
        every { useCases.getQuestionsCount(any()) } returns flowOf(fullPool.size)

        every { billingRepository.purchaseResult } returns emptyFlow()
        every { billingRepository.availableProducts } returns flowOf(emptyList())

        every { premiumStatusProvider.ownedProductIds } returns ownedProductIds
        every { premiumStatusProvider.pendingProductIds } returns pendingProductIds
        every { premiumStatusProvider.isAdsFree } returns flowOf(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(isTrial: Boolean = false) = TranslationQuizViewModel(
        useCases = useCases,
        billingRepository = billingRepository,
        premiumStatusProvider = premiumStatusProvider,
        feedbackManager = NoOpFeedbackManager,
        analyticsLogger = analyticsLogger,
        purchaseFunnelTracker = purchaseFunnelTracker,
        savedStateHandle = SavedStateHandle(mapOf("isTrial" to isTrial)),
    )

    /** Odpowiada na biezace pytanie (poprawnie albo nie) i przechodzi dalej. */
    private fun TranslationQuizViewModel.answer(correctly: Boolean) {
        val current = state.value.currentQuestion!!
        val text = if (correctly) current.possibleTranslations.first() else "zle"
        onEvent(TranslationQuizEvents.OnAnswerChanged(text))
        onEvent(TranslationQuizEvents.OnSubmitAnswer)
        onEvent(TranslationQuizEvents.OnNextQuestion)
    }

    // region jednorazowosc startu

    @Test
    fun `a trial session starts as a free preview`() = runTest {
        createViewModel(isTrial = true)

        val started = analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().single()
        assertEquals("translations", started.mode)
        assertEquals(QuizType.FREE_PREVIEW, started.quizType)
        assertTrue(started.isFreePreview)
        assertEquals(trialPool.size, started.questionCount)
        assertEquals(1, analyticsLogger.eventsOfType<AnalyticsEvent.TrialStarted>().size)
    }

    @Test
    fun `repeated emissions of the question flow do not duplicate the start`() = runTest {
        every { useCases.getTranslationQuestions() } returns
                flowOf(Response.Success(fullPool), Response.Success(fullPool))

        createViewModel()

        val started = analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().single()
        assertEquals(QuizType.FULL, started.quizType)
        assertEquals(fullPool.size, started.questionCount)
    }

    // endregion

    // region konwersja triala

    /**
     * Po zakupie `loadData(preserveProgress = true)` dokleja pelna pule do istniejacej kolejki
     * i wznawia od nastepnego pytania. Start nie moze polecec drugi raz, a koniec ma raportowac
     * typ, z jakim sesja ruszyla (ustalone z iOS).
     */
    @Test
    fun `buying during the trial keeps one session with the frozen free preview type`() = runTest {
        val viewModel = createViewModel(isTrial = true)
        viewModel.answer(correctly = true)
        assertTrue(viewModel.state.value.showTrialFinishedPanel)

        ownedProductIds.value = setOf(BillingIds.ID_TRANSLATION_MODE)

        assertFalse(viewModel.state.value.isTrial)
        assertEquals(fullPool.size, viewModel.state.value.questions.size)
        assertEquals(1, analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().size)

        repeat(fullPool.size - trialPool.size) { viewModel.answer(correctly = true) }

        val completed = analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().single()
        assertEquals(QuizType.FREE_PREVIEW, completed.quizType)
        assertTrue(completed.isFreePreview)
        assertFalse(completed.isEarlyExit)
        assertEquals(fullPool.size, completed.questionCount)
        assertEquals(fullPool.size, completed.answeredCount)
        assertEquals(fullPool.size, completed.correctCount)
    }

    @Test
    fun `the trial wall is reported once even when the end of the pool is reached again`() = runTest {
        val viewModel = createViewModel(isTrial = true)
        viewModel.answer(correctly = true)

        viewModel.onEvent(TranslationQuizEvents.OnNextQuestion)

        val wall = analyticsLogger.eventsOfType<AnalyticsEvent.TrialWallReached>().single()
        assertEquals(trialPool.size, wall.answeredCount)
        val paywall = analyticsLogger.eventsOfType<AnalyticsEvent.PaywallViewed>().single()
        assertEquals(Paywall.TRIAL_END, paywall.paywall)
        assertEquals("translations", paywall.mode)
        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().isEmpty())
    }

    // endregion

    // region max_streak

    @Test
    fun `max streak is the longest run of correct answers in the session`() = runTest {
        val viewModel = createViewModel()

        viewModel.answer(correctly = true)
        viewModel.answer(correctly = true)
        viewModel.answer(correctly = false)
        viewModel.answer(correctly = true)

        val completed = analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().single()
        assertEquals(2, completed.maxStreak)
        assertEquals(3, completed.correctCount)
        assertEquals(1L, completed.params["incorrect_count"])
    }

    // endregion

    // region domkniecie sesji

    @Test
    fun `leaving while the questions are still loading closes nothing`() = runTest {
        every { useCases.getTranslationQuestions() } returns flowOf(Response.Loading)
        val viewModel = createViewModel()

        viewModel.onEvent(TranslationQuizEvents.OnBackConfirmed {})

        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().isEmpty())
        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().isEmpty())
    }

    @Test
    fun `leaving before the first answer closes the funnel without an end screen`() = runTest {
        val viewModel = createViewModel()
        var navigatedBack = false

        viewModel.onEvent(TranslationQuizEvents.OnBackConfirmed { navigatedBack = true })

        val completed = analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().single()
        assertTrue(completed.isEarlyExit)
        assertEquals(0, completed.answeredCount)
        assertTrue(navigatedBack)
        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.ScreenView>().isEmpty())
    }

    @Test
    fun `the end screen is reported once with the mode`() = runTest {
        val viewModel = createViewModel()
        repeat(fullPool.size) { viewModel.answer(correctly = true) }

        val end = analyticsLogger.eventsOfType<AnalyticsEvent.ScreenView>().single()
        assertEquals("quiz_end", end.screenName)
        assertEquals("translations", end.mode)
        assertTrue(viewModel.state.value.isQuizFinished)
        assertEquals(
            listOf("quiz_start", "quiz_complete", "screen_view"),
            analyticsLogger.eventNames(),
        )
    }

    // endregion
}

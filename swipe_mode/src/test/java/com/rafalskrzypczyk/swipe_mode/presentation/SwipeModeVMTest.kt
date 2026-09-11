package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.lifecycle.SavedStateHandle
import com.rafalskrzypczyk.billing.analytics.PurchaseFunnelTracker
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.Paywall
import com.rafalskrzypczyk.core.analytics.QuizType
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeUseCases
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
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
 * Niezmienniki analityki sesji Swipe. Ten tryb ma najkruchsze bramki z calej aplikacji:
 * jednorazowosc `quiz_start` trzyma `quizStartTime == 0L` mimo `collectLatest` z Firestore
 * i ponownego `loadQuestions()` po zakupie w trialu, a `bestStreak` startuje od rekordu wszech
 * czasow, wiec `max_streak` potrzebuje osobnego licznika.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwipeModeVMTest {

    private lateinit var useCases: SwipeModeUseCases
    private lateinit var adHandler: QuizAdHandler
    private lateinit var billingRepository: BillingRepository
    private lateinit var premiumStatusProvider: PremiumStatusProvider
    private lateinit var analyticsLogger: RecordingAnalyticsLogger
    private lateinit var purchaseFunnelTracker: PurchaseFunnelTracker

    private val ownedProductIds = MutableStateFlow<Set<String>>(emptySet())
    private val pendingProductIds = MutableStateFlow<Set<String>>(emptySet())

    private val trialPool = listOf(
        SwipeQuestion(id = 1L, text = "T1", isCorrect = true, isFree = true),
        SwipeQuestion(id = 2L, text = "T2", isCorrect = false, isFree = true),
    )
    private val fullPool = trialPool + listOf(
        SwipeQuestion(id = 3L, text = "F3", isCorrect = true, isFree = false),
        SwipeQuestion(id = 4L, text = "F4", isCorrect = true, isFree = false),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        useCases = mockk(relaxed = true)
        adHandler = mockk(relaxed = true)
        billingRepository = mockk(relaxed = true)
        premiumStatusProvider = mockk(relaxed = true)
        analyticsLogger = RecordingAnalyticsLogger()
        purchaseFunnelTracker = mockk(relaxed = true)

        every { useCases.getUserScore() } returns flowOf(Score.empty())
        every { useCases.getShuffledSwipeQuestions() } returns flowOf(Response.Success(fullPool))
        every { useCases.getShuffledSwipeTrialQuestions() } returns flowOf(Response.Success(trialPool))
        every { useCases.getUpdatedSwipeQuestions() } returns emptyFlow()
        every { useCases.getUpdatedSwipeTrialQuestions() } returns emptyFlow()
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

    private fun createViewModel(isTrial: Boolean = false) = SwipeModeVM(
        useCases = useCases,
        adHandler = adHandler,
        billingRepository = billingRepository,
        premiumStatusProvider = premiumStatusProvider,
        feedbackManager = NoOpFeedbackManager,
        analyticsLogger = analyticsLogger,
        purchaseFunnelTracker = purchaseFunnelTracker,
        savedStateHandle = SavedStateHandle(mapOf("isTrial" to isTrial)),
    )

    /** Karta na wierzchu to ostatni element pary. */
    private fun SwipeModeVM.currentQuestionId(): Long = state.value.questionsPair.last().id

    /** Swipe zgodny z prawda pytania = odpowiedz poprawna; przeciwny = bledna. */
    private fun SwipeModeVM.answer(correctly: Boolean) {
        val id = currentQuestionId()
        val question = fullPool.first { it.id == id }
        onEvent(SwipeModeUIEvents.SubmitAnswer(id, isCorrect = question.isCorrect == correctly))
    }

    // region jednorazowosc startu

    @Test
    fun `a trial session starts as a free preview`() = runTest {
        val viewModel = createViewModel(isTrial = true)

        val started = analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().single()
        assertEquals("swipe", started.mode)
        assertEquals(QuizType.FREE_PREVIEW, started.quizType)
        assertTrue(started.isFreePreview)
        assertEquals(trialPool.size, started.questionCount)
        assertEquals(1, analyticsLogger.eventsOfType<AnalyticsEvent.TrialStarted>().size)
        assertTrue(viewModel.state.value.isTrial)
    }

    @Test
    fun `a full session starts with the full pool and no trial event`() = runTest {
        createViewModel(isTrial = false)

        val started = analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().single()
        assertEquals(QuizType.FULL, started.quizType)
        assertFalse(started.isFreePreview)
        assertEquals(fullPool.size, started.questionCount)
        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.TrialStarted>().isEmpty())
    }

    @Test
    fun `repeated emissions of the question flow do not duplicate the start`() = runTest {
        every { useCases.getShuffledSwipeQuestions() } returns
                flowOf(Response.Success(fullPool), Response.Success(fullPool))

        createViewModel()

        assertEquals(1, analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().size)
    }

    // endregion

    // region konwersja triala

    /**
     * Zakup w trakcie triala przeladowuje pytania przez ten sam `loadQuestions()`, ktory
     * wystartowal sesje — drugi `quiz_start` nie moze polecec, a `quiz_complete` ma raportowac
     * typ, z jakim sesja ruszyla (ustalone z iOS).
     */
    @Test
    fun `buying during the trial keeps one session with the frozen free preview type`() = runTest {
        val viewModel = createViewModel(isTrial = true)
        viewModel.answer(correctly = true)
        viewModel.answer(correctly = true)
        viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)
        assertTrue(viewModel.state.value.showTrialFinishedPanel)

        ownedProductIds.value = setOf(BillingIds.ID_SWIPE_MODE)

        assertFalse(viewModel.state.value.isTrial)
        assertEquals(1, analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().size)

        viewModel.answer(correctly = true)
        viewModel.answer(correctly = true)
        viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)

        val completed = analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().single()
        assertEquals(QuizType.FREE_PREVIEW, completed.quizType)
        assertTrue(completed.isFreePreview)
        assertFalse(completed.isEarlyExit)
        // Pula w chwili zakonczenia, ale odpowiedzi nigdy jej nie przekrocza.
        assertEquals(fullPool.size, completed.questionCount)
        assertEquals(fullPool.size, completed.answeredCount)
    }

    @Test
    fun `the trial wall is reported once even when the end of the pool is reached again`() = runTest {
        val viewModel = createViewModel(isTrial = true)
        viewModel.answer(correctly = true)
        viewModel.answer(correctly = true)

        viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)
        viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)

        val wall = analyticsLogger.eventsOfType<AnalyticsEvent.TrialWallReached>().single()
        assertEquals(trialPool.size, wall.answeredCount)
        val paywall = analyticsLogger.eventsOfType<AnalyticsEvent.PaywallViewed>().single()
        assertEquals(Paywall.TRIAL_END, paywall.paywall)
        assertEquals("swipe", paywall.mode)
        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().isEmpty())
    }

    // endregion

    // region max_streak

    /** `bestStreak` jest zasiewany rekordem wszech czasow — `max_streak` ma go nie widziec. */
    @Test
    fun `max streak counts this session only, not the all-time combo`() = runTest {
        every { useCases.getUserScore() } returns flowOf(Score.empty().copy(bestSwipeCombo = 10))
        val viewModel = createViewModel()

        viewModel.answer(correctly = true)
        viewModel.answer(correctly = true)
        viewModel.answer(correctly = false)
        viewModel.answer(correctly = true)
        viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)

        val completed = analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().single()
        assertEquals(2, completed.maxStreak)
        assertEquals(3, completed.correctCount)
        assertEquals(10, viewModel.state.value.bestStreak)
    }

    // endregion

    // region domkniecie sesji

    @Test
    fun `leaving while the questions are still loading closes nothing`() = runTest {
        every { useCases.getShuffledSwipeQuestions() } returns flowOf(Response.Loading)
        val viewModel = createViewModel()

        viewModel.onEvent(SwipeModeUIEvents.OnBackConfirmed {})

        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.QuizStarted>().isEmpty())
        assertTrue(analyticsLogger.eventsOfType<AnalyticsEvent.QuizCompleted>().isEmpty())
    }

    @Test
    fun `leaving before the first answer closes the funnel without an end screen`() = runTest {
        val viewModel = createViewModel()
        var navigatedBack = false

        viewModel.onEvent(SwipeModeUIEvents.OnBackConfirmed { navigatedBack = true })

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

        viewModel.onEvent(SwipeModeUIEvents.OnFinalFeedbackFinished)

        val end = analyticsLogger.eventsOfType<AnalyticsEvent.ScreenView>().single()
        assertEquals("quiz_end", end.screenName)
        assertEquals("swipe", end.mode)
        assertTrue(viewModel.state.value.isQuizFinished)
        assertEquals(
            listOf("quiz_start", "quiz_complete", "screen_view"),
            analyticsLogger.eventNames(),
        )
    }

    // endregion
}

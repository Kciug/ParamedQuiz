package com.rafalskrzypczyk.home_screen.presentation.home_page

import com.rafalskrzypczyk.billing.analytics.PurchaseFunnelTracker
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.HomeAddon
import com.rafalskrzypczyk.core.analytics.PurchaseSurface
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import com.rafalskrzypczyk.core.feedback.NoOpFeedbackManager
import com.rafalskrzypczyk.home_screen.domain.HomeScreenUseCases
import com.rafalskrzypczyk.notifications.ContentTopicManager
import com.rafalskrzypczyk.notifications.ReminderScheduler
import com.rafalskrzypczyk.core.utils.QuizMode
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
class HomeScreenVMTest {

    private lateinit var useCases: HomeScreenUseCases
    private lateinit var premiumStatusProvider: PremiumStatusProvider
    private lateinit var billingRepository: BillingRepository
    private lateinit var reminderScheduler: ReminderScheduler
    private lateinit var contentTopicManager: ContentTopicManager
    private lateinit var analyticsLogger: RecordingAnalyticsLogger
    private lateinit var purchaseFunnelTracker: PurchaseFunnelTracker
    private lateinit var viewModel: HomeScreenVM

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        
        useCases = mockk(relaxed = true)
        premiumStatusProvider = mockk(relaxed = true)
        billingRepository = mockk(relaxed = true)
        reminderScheduler = mockk(relaxed = true)
        contentTopicManager = mockk(relaxed = true)
        analyticsLogger = RecordingAnalyticsLogger()
        purchaseFunnelTracker = mockk(relaxed = true)

        every { billingRepository.availableProducts } returns flowOf(emptyList())
        every { useCases.getUserScore() } returns flowOf(mockk(relaxed = true))
        every { useCases.getUserData() } returns flowOf(mockk(relaxed = true))
        every { premiumStatusProvider.ownedProductIds } returns flowOf(emptySet())
        
        viewModel = HomeScreenVM(
            useCases,
            premiumStatusProvider,
            billingRepository,
            reminderScheduler,
            contentTopicManager,
            NoOpFeedbackManager,
            analyticsLogger,
            purchaseFunnelTracker,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `mode tap is reported with the lock state from the home state`() = runTest {
        viewModel.onEvent(HomeUIEvents.ModeSelected(QuizMode.SwipeMode, locked = true))

        val selected = analyticsLogger.eventsOfType<AnalyticsEvent.ModeSelected>().single()
        assertEquals("swipe", selected.mode)
        assertTrue(selected.locked)
    }

    @Test
    fun `addon tap is reported with its availability`() = runTest {
        viewModel.onEvent(HomeUIEvents.AddonTapped(HomeAddon.DAILY, available = false))

        val tapped = analyticsLogger.eventsOfType<AnalyticsEvent.AddonTapped>().single()
        assertEquals(HomeAddon.DAILY, tapped.addon)
        assertEquals(false, tapped.available)
    }

    @Test
    fun `opening the mode sheet reports a paywall view`() = runTest {
        viewModel.onEvent(HomeUIEvents.OpenSwipeModePurchaseSheet)

        val shown = analyticsLogger.eventsOfType<AnalyticsEvent.PaywallShown>().single()
        assertEquals(PurchaseSurface.HOME_SHEET, shown.surface)
        assertEquals(BillingIds.ID_SWIPE_MODE, shown.productId)
    }

    @Test
    fun `OpenTranslationModePurchaseSheet triggers queryProducts`() = runTest {
        viewModel.onEvent(HomeUIEvents.OpenTranslationModePurchaseSheet)
        
        coVerify { billingRepository.queryProducts(listOf(BillingIds.ID_TRANSLATION_MODE)) }
    }
}

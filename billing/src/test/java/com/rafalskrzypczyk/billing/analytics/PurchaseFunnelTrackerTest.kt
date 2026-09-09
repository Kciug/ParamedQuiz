package com.rafalskrzypczyk.billing.analytics

import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.PurchaseSurface
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import com.rafalskrzypczyk.core.utils.TimeProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class PurchaseFunnelTrackerTest {

    private lateinit var resultFlow: MutableSharedFlow<PurchaseResult>
    private lateinit var analytics: RecordingAnalyticsLogger
    private lateinit var timeProvider: MutableTimeProvider
    private lateinit var tracker: PurchaseFunnelTracker

    private val swipeMode = AppProduct(
        id = BillingIds.ID_SWIPE_MODE,
        name = "Tryb Swipe",
        description = "",
        price = "9,99 zl",
        priceAmountMicros = 9_990_000L,
        priceCurrencyCode = "PLN",
    )

    @Before
    fun setUp() {
        resultFlow = MutableSharedFlow(extraBufferCapacity = 8)
        val billingRepository = mockk<BillingRepository>(relaxed = true)
        every { billingRepository.purchaseResult } returns resultFlow

        analytics = RecordingAnalyticsLogger()
        timeProvider = MutableTimeProvider()

        tracker = PurchaseFunnelTracker(
            billingRepository = billingRepository,
            analyticsLogger = analytics,
            timeProvider = timeProvider,
            externalScope = CoroutineScope(UnconfinedTestDispatcher()),
        )
        tracker.start()
    }

    private fun emit(result: PurchaseResult) {
        assertTrue("emisja wyniku zakupu nie doszla", resultFlow.tryEmit(result))
    }

    @Test
    fun `pending keeps the attribution so the later success still carries revenue`() {
        tracker.onPurchaseStarted(PurchaseSurface.STORE, swipeMode)
        emit(PurchaseResult.Pending(BillingIds.ID_SWIPE_MODE))
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(PurchaseSurface.STORE, completed.surface)
        assertEquals(9.99, completed.value, 0.001)
        assertEquals(1, analytics.eventsOfType<AnalyticsEvent.PurchaseStandard>().size)
    }

    @Test
    fun `purchase start is logged with the surface and price of the product`() {
        tracker.onPurchaseStarted(PurchaseSurface.TRIAL_END, swipeMode)

        val started = analytics.eventsOfType<AnalyticsEvent.PurchaseStarted>().single()
        assertEquals(PurchaseSurface.TRIAL_END, started.surface)
        assertEquals(BillingIds.ID_SWIPE_MODE, started.productId)
        assertEquals(9_990_000L, started.priceMicros)
        assertEquals("PLN", started.currency)
    }

    @Test
    fun `success after a start carries the remembered surface and revenue`() {
        tracker.onPurchaseStarted(PurchaseSurface.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(PurchaseSurface.STORE, completed.surface)
        assertEquals(9.99, completed.value, 0.001)
        assertEquals("PLN", completed.currency)

        val standard = analytics.eventsOfType<AnalyticsEvent.PurchaseStandard>().single()
        assertEquals(BillingIds.ID_SWIPE_MODE, standard.productId)
        assertEquals(9.99, standard.value, 0.001)
    }

    @Test
    fun `cancellation is attributed to the remembered product`() {
        tracker.onPurchaseStarted(PurchaseSurface.HOME_SHEET, swipeMode)
        emit(PurchaseResult.Cancelled)

        val cancelled = analytics.eventsOfType<AnalyticsEvent.PurchaseCancelled>().single()
        assertEquals(PurchaseSurface.HOME_SHEET, cancelled.surface)
        assertEquals(BillingIds.ID_SWIPE_MODE, cancelled.productId)
    }

    @Test
    fun `error carries the billing error code`() {
        tracker.onPurchaseStarted(PurchaseSurface.CATEGORY_SHEET, swipeMode)
        emit(PurchaseResult.Error(AppError.Billing.ItemAlreadyOwned))

        val failed = analytics.eventsOfType<AnalyticsEvent.PurchaseFailed>().single()
        assertEquals(PurchaseSurface.CATEGORY_SHEET, failed.surface)
        assertEquals(BillingIds.ID_SWIPE_MODE, failed.productId)
        assertEquals("item_already_owned", failed.errorCode)
    }

    @Test
    fun `pending is reported with the product from the result`() {
        tracker.onPurchaseStarted(PurchaseSurface.STORE, swipeMode)
        emit(PurchaseResult.Pending(BillingIds.ID_SWIPE_MODE))

        val pending = analytics.eventsOfType<AnalyticsEvent.PurchasePending>().single()
        assertEquals(PurchaseSurface.STORE, pending.surface)
        assertEquals(BillingIds.ID_SWIPE_MODE, pending.productId)
    }

    @Test
    fun `redelivered success does not inflate revenue`() {
        tracker.onPurchaseStarted(PurchaseSurface.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        assertEquals(1, analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().size)
        assertEquals(1, analytics.eventsOfType<AnalyticsEvent.PurchaseStandard>().size)
    }

    @Test
    fun `result without a preceding start lands in the unknown bucket`() {
        emit(PurchaseResult.Success(BillingIds.ID_FULL_PACKAGE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(PurchaseSurface.UNKNOWN, completed.surface)
        assertEquals(BillingIds.ID_FULL_PACKAGE, completed.productId)
        // Bez znanej ceny nie wysylamy standardowego `purchase` — zanizalby raport przychodu.
        assertTrue(analytics.eventsOfType<AnalyticsEvent.PurchaseStandard>().isEmpty())
    }

    @Test
    fun `a stale start no longer attributes the surface`() {
        tracker.onPurchaseStarted(PurchaseSurface.STORE, swipeMode)
        timeProvider.millis += 31 * 60 * 1000L

        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(PurchaseSurface.UNKNOWN, completed.surface)
    }

    @Test
    fun `a result for another product does not consume the pending start`() {
        tracker.onPurchaseStarted(PurchaseSurface.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_FULL_PACKAGE))
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>()
        assertEquals(2, completed.size)
        assertEquals(PurchaseSurface.UNKNOWN, completed[0].surface)
        assertEquals(PurchaseSurface.STORE, completed[1].surface)
    }

    private class MutableTimeProvider(var millis: Long = 0L) : TimeProvider {
        override fun now(): Date = Date(millis)
    }
}

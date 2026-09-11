package com.rafalskrzypczyk.billing.analytics

import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.getCategoryBillingId
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.Paywall
import com.rafalskrzypczyk.core.analytics.ProductType
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
    fun `pending keeps the attribution so the later success is still attributed`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Pending(BillingIds.ID_SWIPE_MODE))
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(Paywall.STORE, completed.paywall)
    }

    /**
     * Przychod raportuje automatyczne `in_app_purchase`. GA4 nie deduplikuje go z recznie
     * wyslanym `purchase` na strumieniach aplikacyjnych, wiec drugie zdarzenie przychodowe
     * podwoiloby raport.
     */
    @Test
    fun `no revenue event is emitted alongside the funnel marker`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        assertEquals(listOf("purchase_start", "purchase_complete"), analytics.eventNames())
    }

    @Test
    fun `purchase start is logged with the paywall and price of the product`() {
        tracker.onPurchaseStarted(Paywall.TRIAL_END, swipeMode)

        val started = analytics.eventsOfType<AnalyticsEvent.PurchaseStarted>().single()
        assertEquals(Paywall.TRIAL_END, started.paywall)
        assertEquals(BillingIds.ID_SWIPE_MODE, started.productId)
        assertEquals(ProductType.MODE, started.productType)
        assertEquals(9_990_000L, started.priceMicros)
        assertEquals("PLN", started.currency)
    }

    @Test
    fun `success after a start carries the remembered paywall and product type`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(Paywall.STORE, completed.paywall)
        assertEquals(BillingIds.ID_SWIPE_MODE, completed.productId)
        assertEquals(ProductType.MODE, completed.productType)
    }

    @Test
    fun `a completed category purchase carries the category id from its sku`() {
        val category = swipeMode.copy(id = getCategoryBillingId(42L))
        tracker.onPurchaseStarted(Paywall.CATEGORY, category)
        emit(PurchaseResult.Success(category.id))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(ProductType.CATEGORY, completed.productType)
        assertEquals(42L, completed.categoryId)
        assertEquals(null, completed.mode)
    }

    @Test
    fun `a completed mode purchase carries the mode dictionary value`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals("swipe", completed.mode)
        assertEquals(null, completed.categoryId)
    }

    @Test
    fun `cancellation is attributed to the remembered product`() {
        tracker.onPurchaseStarted(Paywall.MODE, swipeMode)
        emit(PurchaseResult.Cancelled)

        val cancelled = analytics.eventsOfType<AnalyticsEvent.PurchaseCancelled>().single()
        assertEquals(Paywall.MODE, cancelled.paywall)
        assertEquals(BillingIds.ID_SWIPE_MODE, cancelled.productId)
    }

    @Test
    fun `error carries the billing error code`() {
        tracker.onPurchaseStarted(Paywall.CATEGORY, swipeMode)
        emit(PurchaseResult.Error(AppError.Billing.ItemAlreadyOwned))

        val failed = analytics.eventsOfType<AnalyticsEvent.PurchaseFailed>().single()
        assertEquals(Paywall.CATEGORY, failed.paywall)
        assertEquals(BillingIds.ID_SWIPE_MODE, failed.productId)
        assertEquals("item_already_owned", failed.errorCode)
    }

    @Test
    fun `pending is reported with the product from the result`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Pending(BillingIds.ID_SWIPE_MODE))

        val pending = analytics.eventsOfType<AnalyticsEvent.PurchasePending>().single()
        assertEquals(Paywall.STORE, pending.paywall)
        assertEquals(BillingIds.ID_SWIPE_MODE, pending.productId)
    }

    @Test
    fun `redelivered success is reported once`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        assertEquals(1, analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().size)
    }

    @Test
    fun `result without a preceding start lands in the unknown bucket`() {
        emit(PurchaseResult.Success(BillingIds.ID_FULL_PACKAGE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(Paywall.UNKNOWN, completed.paywall)
        assertEquals(BillingIds.ID_FULL_PACKAGE, completed.productId)
        // Rodzaj produktu wynika z SKU, wiec zostaje znany takze bez zapamietanego startu.
        assertEquals(ProductType.PREMIUM, completed.productType)
    }

    @Test
    fun `a stale start no longer attributes the paywall`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        timeProvider.millis += 31 * 60 * 1000L

        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>().single()
        assertEquals(Paywall.UNKNOWN, completed.paywall)
    }

    @Test
    fun `a result for another product does not consume the pending start`() {
        tracker.onPurchaseStarted(Paywall.STORE, swipeMode)
        emit(PurchaseResult.Success(BillingIds.ID_FULL_PACKAGE))
        emit(PurchaseResult.Success(BillingIds.ID_SWIPE_MODE))

        val completed = analytics.eventsOfType<AnalyticsEvent.PurchaseCompleted>()
        assertEquals(2, completed.size)
        assertEquals(Paywall.UNKNOWN, completed[0].paywall)
        assertEquals(Paywall.STORE, completed[1].paywall)
    }

    private class MutableTimeProvider(var millis: Long = 0L) : TimeProvider {
        override fun now(): Date = Date(millis)
    }
}

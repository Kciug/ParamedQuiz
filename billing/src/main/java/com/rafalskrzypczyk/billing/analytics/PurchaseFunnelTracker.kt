package com.rafalskrzypczyk.billing.analytics

import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.PurchaseSurface
import com.rafalskrzypczyk.core.error.analyticsCode
import com.rafalskrzypczyk.core.utils.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Jedyne źródło zdarzeń terminalnych lejka zakupowego.
 *
 * Dlaczego nie w ViewModelach: `BillingRepository.purchaseResult` to gorący `SharedFlow`
 * kolekcjonowany równolegle przez pięć ViewModeli, więc logowanie per ViewModel dawałoby
 * duplikaty, gdy dwa żyją naraz. Dodatkowo [PurchaseResult.Cancelled] i [PurchaseResult.Error]
 * nie niosą `productId`, więc bez zapamiętanego startu nie dałoby się ich przypisać do produktu.
 *
 * Ograniczenie, o którym trzeba pamiętać przy zmianach: `_purchaseResult` nie ma bufora ani
 * replay, a `emit()` zawiesza się do odbioru przez **wszystkich** subskrybentów. Ten kolektor
 * leży więc na ścieżce krytycznej dostarczania wyniku zakupu do UI — jego ciało musi być
 * nieblokujące (żadnego `await`, `runBlocking` ani zawieszającego wywołania dostawcy).
 */
@Singleton
class PurchaseFunnelTracker @Inject constructor(
    private val billingRepository: BillingRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val timeProvider: TimeProvider,
    private val externalScope: CoroutineScope,
) {
    private var collectJob: Job? = null

    /** Jednorazowy slot atrybucji: wypełnia go start zakupu, konsumuje pierwszy pasujący wynik. */
    private var startedPurchase: StartedPurchase? = null

    /** Ostatni zaraportowany sukces — chroni przed duplikatem przy redystrybucji zakupu przez Play. */
    private var lastCompleted: CompletedPurchase? = null

    /**
     * Startowane z `ParamedQuizApplication.onCreate`, tak jak `reminderScheduler.ensureScheduled()`.
     *
     * Subskrypcja musi istnieć zanim poleci pierwszy wynik: flow nie ma replay, więc zdarzenia
     * wyemitowane bez subskrybenta przepadają. Z tego samego powodu nie kolekcjonujemy w `init` —
     * singleton powstający leniwie zdążyłby zgubić wynik.
     */
    fun start() {
        if (collectJob != null) return
        collectJob = externalScope.launch {
            billingRepository.purchaseResult.collect { handleResult(it) }
        }
    }

    /** Wołane przez ViewModel tuż przed `launchBillingFlow`. [surface] jest stały dla ViewModelu. */
    fun onPurchaseStarted(surface: PurchaseSurface, product: AppProduct) {
        startedPurchase = StartedPurchase(surface, product, timeProvider.now().time)
        analyticsLogger.log(
            AnalyticsEvent.PurchaseStarted(
                surface = surface,
                productId = product.id,
                priceMicros = product.priceAmountMicros,
                currency = product.priceCurrencyCode,
            )
        )
    }

    private fun handleResult(result: PurchaseResult) {
        when (result) {
            is PurchaseResult.Success -> handleSuccess(result.productId)

            is PurchaseResult.Pending -> {
                val started = consumeStarted(result.productId)
                analyticsLogger.log(
                    AnalyticsEvent.PurchasePending(started.surface(), result.productId)
                )
            }

            PurchaseResult.Cancelled -> {
                val started = consumeStarted(productId = null)
                analyticsLogger.log(
                    AnalyticsEvent.PurchaseCancelled(started.surface(), started.productId())
                )
            }

            is PurchaseResult.Error -> {
                val started = consumeStarted(productId = null)
                analyticsLogger.log(
                    AnalyticsEvent.PurchaseFailed(
                        surface = started.surface(),
                        productId = started.productId(),
                        errorCode = result.error.analyticsCode(),
                    )
                )
            }
        }
    }

    private fun handleSuccess(productId: String) {
        val now = timeProvider.now().time

        val previous = lastCompleted
        if (previous != null && previous.productId == productId &&
            now - previous.atMillis < DEDUPE_WINDOW_MS
        ) {
            // Play potrafi ponownie dostarczyć ten sam zakup przez onPurchasesUpdated, a
            // PurchaseResult nie niesie purchaseToken, po którym dałoby się odróżnić transakcje.
            return
        }
        lastCompleted = CompletedPurchase(productId, now)

        val started = consumeStarted(productId)
        val value = started?.product?.priceAmountMicros?.let { it / MICROS_IN_UNIT }
        val currency = started?.product?.priceCurrencyCode.orEmpty()

        analyticsLogger.log(
            AnalyticsEvent.PurchaseCompleted(
                surface = started.surface(),
                productId = productId,
                value = value ?: 0.0,
                currency = currency,
            )
        )

        // Standardowy `purchase` GA4 tylko ze znaną ceną — bez niej zaniżałby raport przychodu.
        if (value != null && value > 0.0 && currency.isNotEmpty()) {
            analyticsLogger.log(AnalyticsEvent.PurchaseStandard(productId, value, currency))
        }
    }

    /**
     * Zwraca i czyści slot atrybucji. [productId] `null` (anulowanie, błąd) konsumuje dowolny
     * start, bo to jedyny sposób przypisania takiego wyniku do produktu.
     */
    private fun consumeStarted(productId: String?): StartedPurchase? {
        val started = startedPurchase ?: return null

        if (timeProvider.now().time - started.atMillis > ATTRIBUTION_WINDOW_MS) {
            startedPurchase = null
            return null
        }
        if (productId != null && started.product.id != productId) return null

        startedPurchase = null
        return started
    }

    private fun StartedPurchase?.surface(): PurchaseSurface = this?.surface ?: PurchaseSurface.UNKNOWN

    private fun StartedPurchase?.productId(): String = this?.product?.id ?: PRODUCT_UNKNOWN

    private data class StartedPurchase(
        val surface: PurchaseSurface,
        val product: AppProduct,
        val atMillis: Long,
    )

    private data class CompletedPurchase(
        val productId: String,
        val atMillis: Long,
    )

    private companion object {
        const val MICROS_IN_UNIT = 1_000_000.0

        /** Po tym czasie start przestaje przypisywać wynik — inaczej stary ekran fałszowałby `surface`. */
        const val ATTRIBUTION_WINDOW_MS = 30 * 60 * 1000L

        const val DEDUPE_WINDOW_MS = 10 * 60 * 1000L

        const val PRODUCT_UNKNOWN = "unknown"
    }
}

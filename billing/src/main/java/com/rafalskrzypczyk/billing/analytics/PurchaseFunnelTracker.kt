package com.rafalskrzypczyk.billing.analytics

import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.Paywall
import com.rafalskrzypczyk.core.analytics.ProductType
import com.rafalskrzypczyk.core.analytics.productTypeOf
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.core.analytics.analyticsName
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

    /** Wołane przez ViewModel tuż przed `launchBillingFlow`. [paywall] jest stały dla ViewModelu. */
    fun onPurchaseStarted(paywall: Paywall, product: AppProduct) {
        startedPurchase = StartedPurchase(paywall, product, timeProvider.now().time)
        analyticsLogger.log(
            AnalyticsEvent.PurchaseStarted(
                paywall = paywall,
                productId = product.id,
                productType = productTypeFor(product.id),
                priceMicros = product.priceAmountMicros,
                currency = product.priceCurrencyCode,
            )
        )
    }

    private fun productTypeFor(productId: String): ProductType = productTypeOf(
        productId = productId,
        fullPackageId = BillingIds.ID_FULL_PACKAGE,
        adFreeId = BillingIds.ID_AD_FREE,
        modeIds = setOf(BillingIds.ID_SWIPE_MODE, BillingIds.ID_TRANSLATION_MODE),
    )

    /** SKU trybu → wartość słownika `mode`; dla pozostałych produktów parametr pomijamy. */
    private fun modeFor(productId: String): String? = when (productId) {
        BillingIds.ID_SWIPE_MODE -> QuizMode.SwipeMode.analyticsName()
        BillingIds.ID_TRANSLATION_MODE -> QuizMode.TranslationMode.analyticsName()
        else -> null
    }

    /**
     * Odwrotność [getCategoryBillingId]. Bez tego zakup kategorii dałoby się połączyć z jej
     * wyświetleniem tylko przez ręczne parsowanie SKU w raporcie.
     */
    private fun categoryIdFor(productId: String): Long? =
        if (productTypeFor(productId) == ProductType.CATEGORY) {
            productId.removePrefix(BillingIds.ID_PREFIX).toLongOrNull()
        } else {
            null
        }

    private fun handleResult(result: PurchaseResult) {
        when (result) {
            is PurchaseResult.Success -> handleSuccess(result.productId)

            is PurchaseResult.Pending -> {
                // Pending nie jest wynikiem terminalnym — zakup moze sie jeszcze domknac, wiec
                // slot musi przetrwac, inaczej pozniejszy purchase_complete straci paywall.
                val started = peekStarted(result.productId)
                analyticsLogger.log(
                    AnalyticsEvent.PurchasePending(started.paywall(), result.productId)
                )
            }

            PurchaseResult.Cancelled -> {
                val started = consumeStarted(productId = null)
                analyticsLogger.log(
                    AnalyticsEvent.PurchaseCancelled(started.paywall(), started.productId())
                )
            }

            is PurchaseResult.Error -> {
                val started = consumeStarted(productId = null)
                analyticsLogger.log(
                    AnalyticsEvent.PurchaseFailed(
                        paywall = started.paywall(),
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

        // Bez `value` i `currency`: przychod raportuje automatyczne `in_app_purchase`, a GA4 nie
        // deduplikuje go z recznie wyslanym `purchase` na strumieniach aplikacyjnych.
        analyticsLogger.log(
            AnalyticsEvent.PurchaseCompleted(
                paywall = started.paywall(),
                productId = productId,
                productType = productTypeFor(productId),
                mode = modeFor(productId),
                categoryId = categoryIdFor(productId),
            )
        )
    }

    /** Odczyt bez konsumpcji — dla wyników nieterminalnych (Pending). */
    private fun peekStarted(productId: String): StartedPurchase? {
        val started = startedPurchase ?: return null
        if (timeProvider.now().time - started.atMillis > ATTRIBUTION_WINDOW_MS) return null
        return started.takeIf { it.product.id == productId }
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

    private fun StartedPurchase?.paywall(): Paywall = this?.paywall ?: Paywall.UNKNOWN

    private fun StartedPurchase?.productId(): String = this?.product?.id ?: PRODUCT_UNKNOWN

    private data class StartedPurchase(
        val paywall: Paywall,
        val product: AppProduct,
        val atMillis: Long,
    )

    private data class CompletedPurchase(
        val productId: String,
        val atMillis: Long,
    )

    private companion object {
        /** Po tym czasie start przestaje przypisywać wynik — inaczej stary ekran fałszowałby `surface`. */
        const val ATTRIBUTION_WINDOW_MS = 30 * 60 * 1000L

        const val DEDUPE_WINDOW_MS = 10 * 60 * 1000L

        const val PRODUCT_UNKNOWN = "unknown"
    }
}

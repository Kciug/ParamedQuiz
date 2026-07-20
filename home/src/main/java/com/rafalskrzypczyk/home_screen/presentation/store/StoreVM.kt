package com.rafalskrzypczyk.home_screen.presentation.store

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.billing.domain.getCategoryBillingId
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.FeedbackManager
import com.rafalskrzypczyk.main_mode.domain.models.Category
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.GetAllCategoriesUC
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreVM @Inject constructor(
    private val premiumStatusProvider: PremiumStatusProvider,
    private val billingRepository: BillingRepository,
    private val feedbackManager: FeedbackManager,
    private val getAllCategories: GetAllCategoriesUC
) : ViewModel() {

    private val _state = MutableStateFlow(StoreState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<StoreSideEffect>()
    val effect = _effect.asSharedFlow()

    // Surowe źródła danych — łączone w recomputeDerived()
    private var availableProductsCache: List<AppProduct> = emptyList()
    private var paidCategories: List<Category> = emptyList()
    private var ownedIds: Set<String> = emptySet()
    private var pendingIds: Set<String> = emptySet()

    // Stałe produkty sklepu (pakiet + tryby + brak reklam). Kategorie doklejane dynamicznie.
    private val fixedProductIds = listOf(
        BillingIds.ID_FULL_PACKAGE,
        BillingIds.ID_TRANSLATION_MODE,
        BillingIds.ID_SWIPE_MODE,
        BillingIds.ID_AD_FREE
    )

    init {
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                availableProductsCache = products
                recomputeDerived()
            }
        }

        viewModelScope.launch {
            billingRepository.purchaseResult.collectLatest { result ->
                when (result) {
                    is PurchaseResult.Success -> {
                        _state.update { it.copy(isPurchasing = false) }
                    }

                    is PurchaseResult.Pending -> {
                        _state.update { it.copy(isPurchasing = false) }
                    }

                    PurchaseResult.Cancelled -> {
                        _state.update { it.copy(isPurchasing = false, pendingPurchaseModeId = null) }
                    }

                    is PurchaseResult.Error -> {
                        _state.update { it.copy(isPurchasing = false, purchaseError = result.message, pendingPurchaseModeId = null) }
                    }
                }
            }
        }

        viewModelScope.launch {
            combine(
                premiumStatusProvider.ownedProductIds,
                premiumStatusProvider.pendingProductIds
            ) { owned, pending ->
                owned to pending
            }.collectLatest { (owned, pending) ->
                ownedIds = owned
                pendingIds = pending

                val pendingId = _state.value.pendingPurchaseModeId
                if (pendingId != null && owned.contains(pendingId)) {
                    feedbackManager.perform(FeedbackEvent.PURCHASE)
                    _effect.emit(StoreSideEffect.PurchaseSuccess(pendingId))
                    _state.update { it.copy(pendingPurchaseModeId = null) }
                }

                _state.update { it.copy(isPurchasing = false, purchaseError = null) }
                recomputeDerived()
            }
        }

        viewModelScope.launch {
            getAllCategories().collectLatest { response ->
                if (response is Response.Success) {
                    paidCategories = response.data.filter { !it.unlocked }
                    queryAllProducts()
                    recomputeDerived()
                }
            }
        }

        billingRepository.startBillingConnection()
        billingRepository.refreshPurchases()
    }

    /**
     * Jedno źródło prawdy dla pól pochodnych stanu. Łączy: dostępne produkty (ceny),
     * płatne kategorie (Firestore) oraz statusy własności/przetwarzania.
     */
    private fun recomputeDerived() {
        val products = availableProductsCache
        val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)

        val translationUnlocked = hasFull || ownedIds.contains(BillingIds.ID_TRANSLATION_MODE)
        val swipeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_SWIPE_MODE)
        val adFreeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_AD_FREE)

        val packs = paidCategories.map { category ->
            val sku = getCategoryBillingId(category.id)
            StoreCategoryPack(
                id = category.id,
                sku = sku,
                title = category.title,
                description = category.subtitle,
                questionCount = category.questionsCount,
                price = products.find { it.id == sku }?.price,
                isUnlocked = hasFull || ownedIds.contains(sku),
                isPending = pendingIds.contains(sku)
            )
        }

        val unlockedItemsCount =
            listOf(translationUnlocked, swipeUnlocked, adFreeUnlocked).count { it } +
                    packs.count { it.isUnlocked }
        // Pozycje indywidualne: tłumaczenia + swipe + brak reklam + N kategorii (bez pakietu Premium)
        val totalItemsCount = 3 + packs.size

        _state.update {
            it.copy(
                fullPackageProduct = products.find { p -> p.id == BillingIds.ID_FULL_PACKAGE },
                translationModeProduct = products.find { p -> p.id == BillingIds.ID_TRANSLATION_MODE },
                swipeModeProduct = products.find { p -> p.id == BillingIds.ID_SWIPE_MODE },
                adFreeProduct = products.find { p -> p.id == BillingIds.ID_AD_FREE },
                categoryPacks = packs,
                isPremium = hasFull,
                isTranslationModeUnlocked = translationUnlocked,
                isSwipeModeUnlocked = swipeUnlocked,
                isAdFreeUnlocked = adFreeUnlocked,
                isPremiumPending = pendingIds.contains(BillingIds.ID_FULL_PACKAGE),
                isTranslationModePending = pendingIds.contains(BillingIds.ID_TRANSLATION_MODE),
                isSwipeModePending = pendingIds.contains(BillingIds.ID_SWIPE_MODE),
                isAdFreePending = pendingIds.contains(BillingIds.ID_AD_FREE),
                unlockedItemsCount = unlockedItemsCount,
                totalItemsCount = totalItemsCount,
                savingsText = calculateSavings(products, packs.map { p -> p.sku }),
                responseState = if (products.isNotEmpty()) ResponseState.Success else it.responseState
            )
        }
    }

    /**
     * Oszczędność = suma cen dostępnych pozycji indywidualnych (tryby + brak reklam + płatne
     * kategorie) − cena pakietu Premium. Odporna na brakujący/źle skonfigurowany SKU (sumuje
     * tylko obecne z ceną). Zwraca sformatowaną kwotę (np. "4,97 zł") gdy dodatnia, inaczej null.
     */
    private fun calculateSavings(products: List<AppProduct>, categorySkus: List<String>): String? {
        val fullPackage = products.find { it.id == BillingIds.ID_FULL_PACKAGE } ?: return null
        if (fullPackage.priceAmountMicros <= 0L) return null

        val individualIds = listOf(
            BillingIds.ID_TRANSLATION_MODE,
            BillingIds.ID_SWIPE_MODE,
            BillingIds.ID_AD_FREE
        ) + categorySkus

        val sumMicros = individualIds
            .mapNotNull { id -> products.find { it.id == id } }
            .filter { it.priceAmountMicros > 0L }
            .sumOf { it.priceAmountMicros }

        val savingsMicros = sumMicros - fullPackage.priceAmountMicros
        if (savingsMicros <= 0L) return null

        val currencyCode = fullPackage.priceCurrencyCode
        return try {
            // Aplikacja jest wyłącznie po polsku (localeFilters = "pl"), więc formatujemy
            // oszczędność po polsku (np. "4,97 zł") spójnie z cenami z Google Play.
            NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply {
                if (currencyCode.isNotEmpty()) currency = Currency.getInstance(currencyCode)
            }.format(savingsMicros / 1_000_000.0)
        } catch (e: Exception) {
            null
        }
    }

    fun onEvent(event: StoreUIEvents) {
        when (event) {
            StoreUIEvents.GetData -> loadPrices()
            is StoreUIEvents.BuyProduct -> buyProduct(event.activity, event.productId)
            StoreUIEvents.ConsumeError -> _state.update { it.copy(purchaseError = null) }
        }
    }

    private fun loadPrices() {
        _state.update { it.copy(responseState = ResponseState.Loading) }
        queryAllProducts()
    }

    /** Jedno łączone zapytanie: stałe produkty + wszystkie SKU płatnych kategorii. */
    private fun queryAllProducts() {
        val ids = fixedProductIds + paidCategories.map { getCategoryBillingId(it.id) }
        viewModelScope.launch {
            try {
                billingRepository.queryProducts(ids)
                _state.update { currentState ->
                    if (currentState.responseState is ResponseState.Loading) {
                        currentState.copy(responseState = ResponseState.Success)
                    } else currentState
                }
            } catch (e: Exception) {
                _state.update { it.copy(responseState = ResponseState.Error(e.message ?: "Unknown error")) }
            }
        }
    }

    private fun buyProduct(activity: Activity, productId: String) {
        val details = availableProductsCache.find { it.id == productId }
        if (details != null) {
            _state.update { it.copy(isPurchasing = true, purchaseError = null, pendingPurchaseModeId = productId) }
            billingRepository.launchBillingFlow(activity, details)
        } else {
            _state.update { it.copy(purchaseError = "Product details not found.") }
            loadPrices()
        }
    }
}

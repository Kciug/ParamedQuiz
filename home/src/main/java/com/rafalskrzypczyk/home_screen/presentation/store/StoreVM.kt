package com.rafalskrzypczyk.home_screen.presentation.store

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.billing.domain.getCategoryBillingId
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreVM @Inject constructor(
    private val premiumStatusProvider: PremiumStatusProvider,
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StoreState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<StoreSideEffect>()
    val effect = _effect.asSharedFlow()

    private var availableProductsCache: List<AppProduct> = emptyList()
    private val specificCategoryId = 98226763913716L
    
    private val storeProductIds = listOf(
        BillingIds.ID_FULL_PACKAGE,
        BillingIds.ID_TRANSLATION_MODE,
        BillingIds.ID_SWIPE_MODE,
        BillingIds.ID_AD_FREE,
        getCategoryBillingId(specificCategoryId)
    )

    init {
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                availableProductsCache = products
                val fullPackage = products.find { it.id == BillingIds.ID_FULL_PACKAGE }
                val translationMode = products.find { it.id == BillingIds.ID_TRANSLATION_MODE }
                val swipeMode = products.find { it.id == BillingIds.ID_SWIPE_MODE }
                val adFree = products.find { it.id == BillingIds.ID_AD_FREE }
                val category = products.find { it.id == getCategoryBillingId(specificCategoryId) }
                
                _state.update { currentState ->
                    val nextState = currentState.copy(
                        fullPackageProduct = fullPackage,
                        translationModeProduct = translationMode,
                        swipeModeProduct = swipeMode,
                        adFreeProduct = adFree,
                        categoryProduct = category
                    )
                    
                    if (products.isNotEmpty() || nextState.responseState is ResponseState.Loading) {
                        nextState.copy(responseState = ResponseState.Success)
                    } else {
                        nextState
                    }
                }
            }
        }

        viewModelScope.launch {
            billingRepository.purchaseResult.collectLatest { result ->
                when (result) {
                    is PurchaseResult.Success -> {
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
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                val translationUnlocked = hasFull || ownedIds.contains(BillingIds.ID_TRANSLATION_MODE)
                val swipeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_SWIPE_MODE)
                val categoryUnlocked = hasFull || ownedIds.contains(getCategoryBillingId(specificCategoryId))
                val adFreeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_AD_FREE)

                val pendingId = _state.value.pendingPurchaseModeId
                if (pendingId != null && ownedIds.contains(pendingId)) {
                    _effect.emit(StoreSideEffect.PurchaseSuccess(pendingId))
                    _state.update { it.copy(pendingPurchaseModeId = null) }
                } else if (pendingId == BillingIds.ID_FULL_PACKAGE && hasFull) {
                    _effect.emit(StoreSideEffect.PurchaseSuccess(pendingId))
                    _state.update { it.copy(pendingPurchaseModeId = null) }
                }

                _state.update { 
                    it.copy(
                        isPremium = hasFull,
                        isTranslationModeUnlocked = translationUnlocked,
                        isSwipeModeUnlocked = swipeUnlocked,
                        isCategoryUnlocked = categoryUnlocked,
                        isAdFreeUnlocked = adFreeUnlocked,
                        isPurchasing = false,
                        purchaseError = null
                    ) 
                }
            }
        }

        billingRepository.startBillingConnection()
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
        viewModelScope.launch {
            try {
                billingRepository.queryProducts(storeProductIds)
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

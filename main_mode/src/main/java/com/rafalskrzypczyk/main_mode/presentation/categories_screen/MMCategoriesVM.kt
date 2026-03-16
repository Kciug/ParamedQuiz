package com.rafalskrzypczyk.main_mode.presentation.categories_screen

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
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.MMCategoriesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MMCategoriesVM @Inject constructor(
    private val useCases: MMCategoriesUseCases,
    private val billingRepository: BillingRepository,
    private val premiumStatusProvider: PremiumStatusProvider
): ViewModel() {
    private val _state = MutableStateFlow(MMCategoriesState())
    val state = _state.asStateFlow()
    
    private var availableProducts: List<AppProduct> = emptyList()

    init {
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                availableProducts = products
                updatePriceInState()
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
                        _state.update { it.copy(isPurchasing = false, pendingPurchaseCategoryId = null) }
                    }

                    is PurchaseResult.Error -> {
                        _state.update { it.copy(isPurchasing = false, purchaseError = result.message, pendingPurchaseCategoryId = null) }
                    }
                }
            }
        }
        
        billingRepository.startBillingConnection()
        billingRepository.refreshPurchases()
    }

    fun onEvent(event: MMCategoriesUIEvents) {
        when(event) {
            MMCategoriesUIEvents.GetData -> {
                billingRepository.refreshPurchases()
                loadData()
            }
            is MMCategoriesUIEvents.OpenPurchaseDialog -> openPurchaseDialog(event.category)
            MMCategoriesUIEvents.ClosePurchaseDialog -> closePurchaseDialog()
            is MMCategoriesUIEvents.BuyCategory -> buyCategory(event.activity)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadData() {
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { userScore ->
                _state.update {
                    it.copy(
                        userScore = userScore.score,
                        userStreak = userScore.streak,
                        userStreakState = useCases.getStreakState(userScore.lastStreakUpdateDate),
                        isUserLoggedIn = useCases.checkIsUserLoggedIn()
                    )
                }
            }
        }

        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                premiumStatusProvider.ownedProductIds,
                premiumStatusProvider.pendingProductIds
            ) { ownedIds, pendingIds ->
                ownedIds to pendingIds
            }.collectLatest { (ownedIds, pendingIds) ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                
                val selectedCategory = _state.value.selectedCategoryForPurchase
                val isSelectedPending = selectedCategory != null && 
                                        pendingIds.contains(getCategoryBillingId(selectedCategory.id))

                val pendingId = _state.value.pendingPurchaseCategoryId
                if (pendingId != null) {
                    val billingId = getCategoryBillingId(pendingId)
                    if (ownedIds.contains(billingId) || hasFull) {
                        _state.update { it.copy(pendingPurchaseCategoryId = null, isPurchasing = false, purchaseError = null) }
                    }
                }
                
                _state.update { it.copy(isPremium = hasFull, isPending = isSelectedPending) }
            }
        }

        viewModelScope.launch {
            useCases.getAllCategories().flatMapLatest { response ->
                if (response is Response.Success) {
                    kotlinx.coroutines.flow.combine(
                        premiumStatusProvider.ownedProductIds,
                        premiumStatusProvider.pendingProductIds
                    ) { ownedIds, pendingIds ->
                        val updatedCategories = response.data.map { category ->
                            val billingId = getCategoryBillingId(category.id)
                            val hasAccess = category.unlocked || 
                                            ownedIds.contains(BillingIds.ID_FULL_PACKAGE) || 
                                            ownedIds.contains(billingId)
                            val isPending = pendingIds.contains(billingId)
                            category.toUIM(hasAccess = hasAccess, isPending = isPending)
                        }
                        Response.Success(updatedCategories)
                    }
                } else {
                    flowOf(response)
                }
            }.collectLatest { response ->
                when(response) {
                    is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                    Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                    is Response.Success -> {
                        attachCategoriesListener()
                        _state.update { state ->
                            state.copy(
                                responseState = ResponseState.Success,
                                categories = response.data.filterIsInstance<CategoryUIM>()
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun attachCategoriesListener() {
        viewModelScope.launch {
            useCases.getUpdatedCategories().flatMapLatest { categories ->
                kotlinx.coroutines.flow.combine(
                    premiumStatusProvider.ownedProductIds,
                    premiumStatusProvider.pendingProductIds
                ) { ownedIds, pendingIds ->
                    categories.map { category ->
                        val billingId = getCategoryBillingId(category.id)
                        val hasAccess = category.unlocked || 
                                        ownedIds.contains(BillingIds.ID_FULL_PACKAGE) || 
                                        ownedIds.contains(billingId)
                        val isPending = pendingIds.contains(billingId)
                        category.toUIM(hasAccess = hasAccess, isPending = isPending)
                    }
                }
            }.collectLatest { data ->
                _state.update { state ->
                    state.copy(categories = data)
                }
            }
        }
    }
    
    private fun openPurchaseDialog(category: CategoryUIM) {
        val productId = getCategoryBillingId(category.id)
        val details = availableProducts.find { it.id == productId }
        
        _state.update { it.copy(selectedCategoryForPurchase = category, productPrice = details?.price) }
        
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(productId))
        }
    }

    private fun closePurchaseDialog() {
        _state.update { it.copy(
            selectedCategoryForPurchase = null, 
            productPrice = null,
            isPurchasing = false,
            purchaseError = null,
            pendingPurchaseCategoryId = null
        ) }
    }
    
    private fun buyCategory(activity: Activity) {
        val category = state.value.selectedCategoryForPurchase ?: return
        val productId = getCategoryBillingId(category.id)
        val productDetails = availableProducts.find { it.id == productId }
        
        if (productDetails != null) {
            _state.update { it.copy(isPurchasing = true, purchaseError = null, pendingPurchaseCategoryId = category.id) }
            billingRepository.launchBillingFlow(activity, productDetails)
        } else {
            _state.update { it.copy(purchaseError = "Product details not found.") }
        }
    }
    
    private fun updatePriceInState() {
        val category = state.value.selectedCategoryForPurchase ?: return
        val productId = getCategoryBillingId(category.id)
        val details = availableProducts.find { it.id == productId }
        
        val price = details?.price
        _state.update { it.copy(productPrice = price) }
    }
}
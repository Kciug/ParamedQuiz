package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.MMCategoriesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
    
    private var availableProducts: List<ProductDetails> = emptyList()

    init {
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                availableProducts = products
                updatePriceInState()
            }
        }
        
        billingRepository.startBillingConnection()
    }

    fun onEvent(event: MMCategoriesUIEvents) {
        when(event) {
            MMCategoriesUIEvents.GetData -> loadData()
            is MMCategoriesUIEvents.OnUnlockCategory -> onUnlockCategory(event.categoryId)
            MMCategoriesUIEvents.DiscardUnlockCategory -> discardUnlockCategory()
            is MMCategoriesUIEvents.OpenPurchaseDialog -> openPurchaseDialog(event.category)
            MMCategoriesUIEvents.ClosePurchaseDialog -> closePurchaseDialog()
            is MMCategoriesUIEvents.BuyCategory -> buyCategory(event.activity)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { userScore ->
                val user = useCases.getUser()
                _state.update {
                    it.copy(
                        userScore = userScore.score,
                        userStreak = userScore.streak,
                        userStreakState = useCases.getStreakState(userScore.lastStreakUpdateDate),
                        isUserLoggedIn = useCases.checkIsUserLoggedIn(),
                        isPremium = user?.isPremium ?: false
                    )
                }
            }
        }

        viewModelScope.launch {
            combine(
                useCases.getAllCategories(),
                premiumStatusProvider.isAdsFree
            ) { response, isPremium ->
                Pair(response, isPremium)
            }.collectLatest { (response, _) ->
                when(response) {
                    is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                    Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                    is Response.Success -> {
                        attachCategoriesListener()
                        _state.update { state ->
                            state.copy(
                                responseState = ResponseState.Success,
                                categories = response.data.map { category ->
                                    category.toUIM() 
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun attachCategoriesListener() {
        viewModelScope.launch {
            useCases.getUpdatedCategories().collectLatest { data ->
                _state.update { state ->
                    state.copy(categories = data.map { it.toUIM() } )
                }
            }
        }
    }

    private fun onUnlockCategory(categoryId: Long) {
        _state.update { it.copy(unlockCategory = true) }
    }

    private fun discardUnlockCategory() {
        _state.update { it.copy(unlockCategory = false) }
    }
    
    private fun openPurchaseDialog(category: CategoryUIM) {
        _state.update { it.copy(selectedCategoryForPurchase = category, productPrice = null) }
        val productId = "category_${category.id}"
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(productId))
        }
    }

    private fun closePurchaseDialog() {
        _state.update { it.copy(selectedCategoryForPurchase = null, productPrice = null) }
    }
    
    private fun buyCategory(activity: Activity) {
        val category = state.value.selectedCategoryForPurchase ?: return
        val productId = "category_${category.id}"
        val productDetails = availableProducts.find { it.productId == productId }
        
        if (productDetails != null) {
            billingRepository.launchBillingFlow(activity, productDetails)
        }
        closePurchaseDialog()
    }
    
    private fun updatePriceInState() {
        val category = state.value.selectedCategoryForPurchase ?: return
        val productId = "category_${category.id}"
        val details = availableProducts.find { it.productId == productId }
        
        val price = details?.oneTimePurchaseOfferDetails?.formattedPrice
        _state.update { it.copy(productPrice = price) }
    }
}
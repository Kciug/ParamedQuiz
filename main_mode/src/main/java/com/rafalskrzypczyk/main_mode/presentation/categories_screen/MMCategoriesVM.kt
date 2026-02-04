package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.MMCategoriesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    @OptIn(ExperimentalCoroutinesApi::class)
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
            useCases.getAllCategories().flatMapLatest { response ->
                if (response is Response.Success) {
                    premiumStatusProvider.ownedProductIds.map { ownedIds ->
                        val updatedCategories = response.data.map { category ->
                            val hasAccess = ownedIds.contains(BillingIds.ID_FULL_PACKAGE) || 
                                            ownedIds.contains(category.id.toString())
                            category.copy(unlocked = category.unlocked || hasAccess)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun attachCategoriesListener() {
        viewModelScope.launch {
            useCases.getUpdatedCategories().flatMapLatest { categories ->
                premiumStatusProvider.ownedProductIds.map { ownedIds ->
                    categories.map { category ->
                        val hasAccess = ownedIds.contains(BillingIds.ID_FULL_PACKAGE) || 
                                        ownedIds.contains(category.id.toString())
                        category.copy(unlocked = category.unlocked || hasAccess)
                    }
                }
            }.collectLatest { data ->
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
        val productId = category.id.toString()
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(productId))
        }
    }

    private fun closePurchaseDialog() {
        _state.update { it.copy(selectedCategoryForPurchase = null, productPrice = null) }
    }
    
    private fun buyCategory(activity: Activity) {
        val category = state.value.selectedCategoryForPurchase ?: return
        val productId = category.id.toString()
        val productDetails = availableProducts.find { it.id == productId }
        
        if (productDetails != null) {
            billingRepository.launchBillingFlow(activity, productDetails)
        }
        closePurchaseDialog()
    }
    
    private fun updatePriceInState() {
        val category = state.value.selectedCategoryForPurchase ?: return
        val productId = category.id.toString()
        val details = availableProducts.find { it.id == productId }
        
        val price = details?.price
        _state.update { it.copy(productPrice = price) }
    }
}
package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.home_screen.domain.HomeScreenUseCases

@HiltViewModel
class HomeScreenVM @Inject constructor(
    private val useCases: HomeScreenUseCases,
    private val premiumStatusProvider: PremiumStatusProvider,
    private val billingRepository: BillingRepository
): ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()
    
    private var translationModeProductDetails: ProductDetails? = null

    init {
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                translationModeProductDetails = products.find { it.productId == BillingIds.ID_TRANSLATION_MODE }
                _state.update { it.copy(translationModePrice = translationModeProductDetails?.oneTimePurchaseOfferDetails?.formattedPrice) }
            }
        }
        
        billingRepository.startBillingConnection()
    }

    fun onEvent(event: HomeUIEvents) {
        when(event) {
            HomeUIEvents.GetData -> getData()
            HomeUIEvents.OpenTranslationModePurchaseDialog -> openPurchaseDialog()
            HomeUIEvents.CloseTranslationModePurchaseDialog -> closePurchaseDialog()
            is HomeUIEvents.BuyTranslationMode -> buyTranslationMode(event.activity)
        }
    }

    private fun getData() {
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { userScore ->
                _state.update {
                    it.copy(
                        userScore = userScore.score,
                        userStreak = userScore.streak,
                        userStreakState = useCases.getStreakState(userScore.lastStreakUpdateDate),
                        isNewDailyExerciseAvailable = useCases.checkDailyExerciseAvailability(userScore.lastDailyExerciseDate),
                        isUserLoggedIn = useCases.checkIsUserLoggedIn()
                    )
                }
            }
        }
        
        viewModelScope.launch {
            useCases.getUserData().collectLatest { response ->
                if (response is Response.Success) {
                    _state.update { it.copy(userName = response.data?.name) }
                }
            }
        }
        
        viewModelScope.launch {
            premiumStatusProvider.hasAccessTo(BillingIds.ID_TRANSLATION_MODE).collectLatest { hasAccess ->
                _state.update { it.copy(isTranslationModeUnlocked = hasAccess) }
            }
        }
    }
    
    private fun openPurchaseDialog() {
        _state.update { it.copy(showTranslationModePurchaseDialog = true) }
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(BillingIds.ID_TRANSLATION_MODE))
        }
    }

    private fun closePurchaseDialog() {
        _state.update { it.copy(showTranslationModePurchaseDialog = false) }
    }

    private fun buyTranslationMode(activity: Activity) {
        val details = translationModeProductDetails
        if (details != null) {
            billingRepository.launchBillingFlow(activity, details)
        }
        closePurchaseDialog()
    }
}
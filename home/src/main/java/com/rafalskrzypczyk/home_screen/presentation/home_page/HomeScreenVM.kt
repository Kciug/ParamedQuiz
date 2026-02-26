package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
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
    
    private var translationModeProductDetails: AppProduct? = null
    private var swipeModeProductDetails: AppProduct? = null

    init {
        viewModelScope.launch {
            billingRepository.availableProducts.collectLatest { products ->
                translationModeProductDetails = products.find { it.id == BillingIds.ID_TRANSLATION_MODE }
                swipeModeProductDetails = products.find { it.id == BillingIds.ID_SWIPE_MODE }
                _state.update { 
                    it.copy(
                        translationModePrice = translationModeProductDetails?.price,
                        swipeModePrice = swipeModeProductDetails?.price
                    ) 
                }
            }
        }
        
        billingRepository.startBillingConnection()
    }

    fun onEvent(event: HomeUIEvents) {
        when(event) {
            HomeUIEvents.GetData -> getData()
            HomeUIEvents.OpenTranslationModePurchaseSheet -> openPurchaseSheet(BillingIds.ID_TRANSLATION_MODE)
            HomeUIEvents.CloseTranslationModePurchaseSheet -> closePurchaseSheet(BillingIds.ID_TRANSLATION_MODE)
            HomeUIEvents.OpenSwipeModePurchaseSheet -> openPurchaseSheet(BillingIds.ID_SWIPE_MODE)
            HomeUIEvents.CloseSwipeModePurchaseSheet -> closePurchaseSheet(BillingIds.ID_SWIPE_MODE)
            is HomeUIEvents.BuyTranslationMode -> buyMode(event.activity, BillingIds.ID_TRANSLATION_MODE)
            is HomeUIEvents.BuySwipeMode -> buyMode(event.activity, BillingIds.ID_SWIPE_MODE)
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
                    val user = response.data
                    _state.update { 
                        it.copy(
                            userName = user?.name,
                            isPremium = user?.isPremium ?: false
                        ) 
                    }
                }
            }
        }
        
        viewModelScope.launch {
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                _state.update { 
                    it.copy(
                        isTranslationModeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_TRANSLATION_MODE),
                        isSwipeModeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_SWIPE_MODE)
                    ) 
                }
            }
        }

        viewModelScope.launch {
            useCases.getQuestionsCount(com.rafalskrzypczyk.firestore.data.FirestoreCollections.TRANSLATION_QUESTIONS)
                .collectLatest { count ->
                    _state.update { it.copy(translationModeQuestionCount = count) }
                }
        }

        viewModelScope.launch {
            useCases.getQuestionsCount(com.rafalskrzypczyk.firestore.data.FirestoreCollections.SWIPE_QUESTIONS)
                .collectLatest { count ->
                    _state.update { it.copy(swipeModeQuestionCount = count) }
                }
        }
    }
    
    private fun openPurchaseSheet(modeId: String) {
        _state.update { 
            when(modeId) {
                BillingIds.ID_TRANSLATION_MODE -> it.copy(showTranslationModePurchaseSheet = true)
                BillingIds.ID_SWIPE_MODE -> it.copy(showSwipeModePurchaseSheet = true)
                else -> it
            }
        }
        viewModelScope.launch {
            billingRepository.queryProducts(listOf(modeId))
        }
    }

    private fun closePurchaseSheet(modeId: String) {
        _state.update { 
            when(modeId) {
                BillingIds.ID_TRANSLATION_MODE -> it.copy(showTranslationModePurchaseSheet = false)
                BillingIds.ID_SWIPE_MODE -> it.copy(showSwipeModePurchaseSheet = false)
                else -> it
            }
        }
    }

    private fun buyMode(activity: Activity, modeId: String) {
        val details = when(modeId) {
            BillingIds.ID_TRANSLATION_MODE -> translationModeProductDetails
            BillingIds.ID_SWIPE_MODE -> swipeModeProductDetails
            else -> null
        }
        if (details != null) {
            billingRepository.launchBillingFlow(activity, details)
        }
        closePurchaseSheet(modeId)
    }
}
package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.composables.rating.RatingPromptState
import com.rafalskrzypczyk.core.domain.UserFeedback
import com.rafalskrzypczyk.home_screen.domain.HomeScreenUseCases
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
class HomeScreenVM @Inject constructor(
    private val useCases: HomeScreenUseCases,
    private val premiumStatusProvider: PremiumStatusProvider,
    private val billingRepository: BillingRepository
): ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeSideEffect>()
    val effect = _effect.asSharedFlow()
    
    private var translationModeProductDetails: AppProduct? = null
    private var swipeModeProductDetails: AppProduct? = null
    private var pendingPurchaseModeId: String? = null

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
            HomeUIEvents.NavigationConsumed -> consumeNavigation()
            is HomeUIEvents.OnRatingSelected -> handleRatingSelected(event.rating)
            HomeUIEvents.OnDismissRating -> dismissRating()
            HomeUIEvents.OnRateStore -> rateStore()
            HomeUIEvents.OnSendFeedback -> sendFeedback()
            is HomeUIEvents.OnFeedbackChanged -> handleFeedbackChanged(event.feedback)
            HomeUIEvents.OnNeverAskAgain -> neverAskAgain()
            HomeUIEvents.OnBackToRating -> backToRating()
            HomeUIEvents.OnFeedbackSuccessConsumed -> _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
            HomeUIEvents.OnFeedbackErrorConsumed -> _state.update { it.copy(feedbackErrorMessage = null) }
        }
    }

    private fun getData() {
        checkRatingEligibility()
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
                            userName = user?.name
                        ) 
                    }
                }
            }
        }
        
        viewModelScope.launch {
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                val translationUnlocked = hasFull || ownedIds.contains(BillingIds.ID_TRANSLATION_MODE)
                val swipeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_SWIPE_MODE)

                if (translationUnlocked && pendingPurchaseModeId == BillingIds.ID_TRANSLATION_MODE) {
                    pendingPurchaseModeId = null
                    _effect.emit(HomeSideEffect.PurchaseSuccess(BillingIds.ID_TRANSLATION_MODE))
                } else if (swipeUnlocked && pendingPurchaseModeId == BillingIds.ID_SWIPE_MODE) {
                    pendingPurchaseModeId = null
                    _effect.emit(HomeSideEffect.PurchaseSuccess(BillingIds.ID_SWIPE_MODE))
                }

                _state.update { 
                    it.copy(
                        isPremium = hasFull,
                        isTranslationModeUnlocked = translationUnlocked,
                        isSwipeModeUnlocked = swipeUnlocked,
                        isPurchasing = false
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

    private fun checkRatingEligibility() {
        if (useCases.checkAppRatingEligibility()) {
            _state.update { it.copy(ratingPromptState = RatingPromptState.QUESTION) }
        }
    }

    private fun handleRatingSelected(rating: Int) {
        if (rating >= 4) {
            _state.update { it.copy(ratingPromptState = RatingPromptState.POSITIVE_FEEDBACK, ratingValue = rating) }
        } else {
            _state.update { it.copy(ratingPromptState = RatingPromptState.NEGATIVE_FEEDBACK, ratingValue = rating) }
        }
    }

    private fun handleFeedbackChanged(feedback: String) {
        _state.update { it.copy(feedbackText = feedback) }
    }

    private fun dismissRating() {
        if (state.value.ratingPromptState == RatingPromptState.CLOSING_OPTIONS) {
            finalDismiss()
        } else {
            _state.update { it.copy(ratingPromptState = RatingPromptState.CLOSING_OPTIONS) }
        }
    }

    private fun finalDismiss() {
        useCases.dismissAppRating()
        _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
    }

    private fun rateStore() {
        useCases.setAppRated()
        _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
        viewModelScope.launch {
            _effect.emit(HomeSideEffect.LaunchReviewFlow)
        }
    }

    private fun sendFeedback() {
        val currentState = state.value
        val feedback = UserFeedback(
            feedback = currentState.feedbackText,
            rating = currentState.ratingValue
        )
        viewModelScope.launch {
            useCases.saveFeedback(feedback).collectLatest { response ->
                when(response) {
                    is Response.Loading -> {
                        _state.update { it.copy(isSendingFeedback = true, feedbackErrorMessage = null) }
                    }
                    is Response.Success -> {
                        _state.update { it.copy(isSendingFeedback = false, feedbackText = "") }
                        useCases.setAppRated()
                        _effect.emit(HomeSideEffect.FeedbackSuccess)
                    }
                    is Response.Error -> {
                        _state.update { it.copy(isSendingFeedback = false, feedbackErrorMessage = response.error) }
                    }
                }
            }
        }
    }

    private fun neverAskAgain() {
        useCases.disableRatingPrompt()
        _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
    }

    private fun backToRating() {
        _state.update { it.copy(ratingPromptState = RatingPromptState.QUESTION) }
    }
    
    private fun openPurchaseSheet(modeId: String) {
        _state.update { 
            val base = when(modeId) {
                BillingIds.ID_TRANSLATION_MODE -> it.copy(showTranslationModePurchaseSheet = true)
                BillingIds.ID_SWIPE_MODE -> it.copy(showSwipeModePurchaseSheet = true)
                else -> it
            }
            base.copy(purchaseError = null, isPurchasing = false)
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
            }.copy(purchaseError = null, isPurchasing = false)
        }
        pendingPurchaseModeId = null
    }

    private fun buyMode(activity: Activity, modeId: String) {
        val details = when(modeId) {
            BillingIds.ID_TRANSLATION_MODE -> translationModeProductDetails
            BillingIds.ID_SWIPE_MODE -> swipeModeProductDetails
            else -> null
        }
        if (details != null) {
            pendingPurchaseModeId = modeId
            _state.update { it.copy(isPurchasing = true, purchaseError = null) }
            billingRepository.launchBillingFlow(activity, details)
        }
    }

    private fun consumeNavigation() {
    }
}

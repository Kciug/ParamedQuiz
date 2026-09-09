package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.analytics.PurchaseFunnelTracker
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.NotificationPromptAction
import com.rafalskrzypczyk.core.analytics.PurchaseSurface
import com.rafalskrzypczyk.core.analytics.RatingAction
import com.rafalskrzypczyk.core.analytics.analyticsName
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.composables.rating.RatingPromptState
import com.rafalskrzypczyk.core.domain.UserFeedback
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.FeedbackManager
import com.rafalskrzypczyk.home_screen.domain.HomeScreenUseCases
import com.rafalskrzypczyk.notifications.ContentTopicManager
import com.rafalskrzypczyk.notifications.ReminderScheduler
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
    private val billingRepository: BillingRepository,
    private val reminderScheduler: ReminderScheduler,
    private val contentTopicManager: ContentTopicManager,
    private val feedbackManager: FeedbackManager,
    private val analyticsLogger: AnalyticsLogger,
    private val purchaseFunnelTracker: PurchaseFunnelTracker
): ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeSideEffect>()
    val effect = _effect.asSharedFlow()
    
    private var translationModeProductDetails: AppProduct? = null
    private var swipeModeProductDetails: AppProduct? = null
    private var pendingPurchaseModeId: String? = null
    private var hasLoggedRatingAnswer = false

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
                        _state.update { it.copy(isPurchasing = false) }
                        pendingPurchaseModeId = null
                    }

                    is PurchaseResult.Error -> {
                        _state.update { it.copy(isPurchasing = false, purchaseError = result.error) }
                        pendingPurchaseModeId = null
                    }
                }
            }
        }
        
        billingRepository.startBillingConnection()
        billingRepository.refreshPurchases()
    }

    fun onEvent(event: HomeUIEvents) {
        when(event) {
            HomeUIEvents.GetData -> {
                billingRepository.refreshPurchases()
                getData()
            }
            HomeUIEvents.OpenTranslationModePurchaseSheet -> openPurchaseSheet(BillingIds.ID_TRANSLATION_MODE)
            HomeUIEvents.CloseTranslationModePurchaseSheet -> closePurchaseSheet(BillingIds.ID_TRANSLATION_MODE)
            HomeUIEvents.OpenSwipeModePurchaseSheet -> openPurchaseSheet(BillingIds.ID_SWIPE_MODE)
            HomeUIEvents.CloseSwipeModePurchaseSheet -> closePurchaseSheet(BillingIds.ID_SWIPE_MODE)
            is HomeUIEvents.BuyTranslationMode -> buyMode(event.activity, BillingIds.ID_TRANSLATION_MODE)
            is HomeUIEvents.BuySwipeMode -> buyMode(event.activity, BillingIds.ID_SWIPE_MODE)
            HomeUIEvents.NavigationConsumed -> consumeNavigation()
            is HomeUIEvents.ModeSelected -> analyticsLogger.log(
                AnalyticsEvent.ModeSelected(event.mode.analyticsName(), event.locked)
            )
            is HomeUIEvents.AddonTapped -> analyticsLogger.log(
                AnalyticsEvent.AddonTapped(event.addon, event.available)
            )
            is HomeUIEvents.OnRatingSelected -> handleRatingSelected(event.rating)
            HomeUIEvents.OnDismissRating -> dismissRating()
            HomeUIEvents.OnRateStore -> rateStore()
            HomeUIEvents.OnSendFeedback -> sendFeedback()
            is HomeUIEvents.OnFeedbackChanged -> handleFeedbackChanged(event.feedback)
            HomeUIEvents.OnNeverAskAgain -> neverAskAgain()
            HomeUIEvents.OnBackToRating -> backToRating()
            HomeUIEvents.OnFeedbackSuccessConsumed -> _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
            HomeUIEvents.OnFeedbackErrorConsumed -> _state.update { it.copy(feedbackErrorMessage = null) }
            is HomeUIEvents.DismissNews -> dismissNews(event.id)
            HomeUIEvents.OnNotificationConsentAccepted -> onNotificationConsentAccepted()
            HomeUIEvents.OnNotificationConsentDenied -> onNotificationConsentDenied()
            HomeUIEvents.OnNotificationConsentDismissed -> onNotificationConsentDismissed()
            HomeUIEvents.RecheckNotificationConsent -> checkNotificationConsentEligibility()
        }
    }

    private fun dismissNews(id: String) {
        analyticsLogger.log(AnalyticsEvent.NewsBannerDismissed(id))
        useCases.markNewsAsSeen(id)
        _state.update { it.copy(newsBanners = it.newsBanners.filter { banner -> banner.id != id }) }
    }

    private fun getData() {
        checkRatingEligibility()
        checkNotificationConsentEligibility()
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
            kotlinx.coroutines.flow.combine(
                premiumStatusProvider.ownedProductIds,
                premiumStatusProvider.pendingProductIds
            ) { ownedIds, pendingIds ->
                ownedIds to pendingIds
            }.collectLatest { (ownedIds, pendingIds) ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                val translationUnlocked = hasFull || ownedIds.contains(BillingIds.ID_TRANSLATION_MODE)
                val swipeUnlocked = hasFull || ownedIds.contains(BillingIds.ID_SWIPE_MODE)

                val isTranslationPending = pendingIds.contains(BillingIds.ID_TRANSLATION_MODE)
                val isSwipePending = pendingIds.contains(BillingIds.ID_SWIPE_MODE)

                if (translationUnlocked && pendingPurchaseModeId == BillingIds.ID_TRANSLATION_MODE) {
                    pendingPurchaseModeId = null
                    feedbackManager.perform(FeedbackEvent.PURCHASE)
                    _effect.emit(HomeSideEffect.PurchaseSuccess(BillingIds.ID_TRANSLATION_MODE))
                } else if (swipeUnlocked && pendingPurchaseModeId == BillingIds.ID_SWIPE_MODE) {
                    pendingPurchaseModeId = null
                    feedbackManager.perform(FeedbackEvent.PURCHASE)
                    _effect.emit(HomeSideEffect.PurchaseSuccess(BillingIds.ID_SWIPE_MODE))
                }

                _state.update { 
                    it.copy(
                        isPremium = hasFull,
                        isTranslationModeUnlocked = translationUnlocked,
                        isSwipeModeUnlocked = swipeUnlocked,
                        isTranslationModePending = isTranslationPending,
                        isSwipeModePending = isSwipePending,
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

        viewModelScope.launch {
            useCases.getNewsBanners().collectLatest { response ->
                if (response is Response.Success) {
                    _state.update { it.copy(newsBanners = response.data) }
                }
            }
        }
    }

    private fun checkRatingEligibility() {
        if (useCases.checkAppRatingEligibility()) {
            // Warunek nie zapisuje faktu pokazania (w odroznieniu od promptu powiadomien), a
            // metoda leci na kazdym wejsciu na Home — bez tej bramki liczylibysmy wejscia, nie
            // pojawienia sie karty.
            if (state.value.ratingPromptState == RatingPromptState.HIDDEN) {
                hasLoggedRatingAnswer = false
                analyticsLogger.log(AnalyticsEvent.RatingPromptShown)
            }
            _state.update { it.copy(ratingPromptState = RatingPromptState.QUESTION) }
        }
    }

    private fun checkNotificationConsentEligibility() {
        // Priming (dialog modalny) i prompt oceny (karta) żyją na różnych warstwach — mogą współistnieć.
        if (useCases.checkNotificationConsentEligibility()) {
            analyticsLogger.log(AnalyticsEvent.NotificationPromptShown)
            useCases.markNotificationPromptShown()
            _state.update { it.copy(showNotificationConsentPrompt = true) }
        }
    }

    private fun onNotificationConsentAccepted() {
        // Zgoda na prompt wewnetrzny — systemowe uprawnienie POST_NOTIFICATIONS rozstrzyga sie
        // warstwe wyzej, w composable.
        logNotificationPromptAnswered(NotificationPromptAction.ACCEPTED)
        useCases.setNotificationsEnabled(true)
        reminderScheduler.schedule()
        contentTopicManager.ensureSubscription()
        _state.update { it.copy(showNotificationConsentPrompt = false) }
    }

    private fun onNotificationConsentDenied() {
        logNotificationPromptAnswered(NotificationPromptAction.DENIED)
        useCases.disableNotificationPrompt()
        _state.update { it.copy(showNotificationConsentPrompt = false) }
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
        // dismissRating() jest dwustopniowe — logujemy dopiero decyzje domykajaca karte.
        logRatingAnswered(RatingAction.DISMISS)
        useCases.dismissAppRating()
        _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
    }

    private fun rateStore() {
        logRatingAnswered(RatingAction.STORE)
        useCases.setAppRated()
        _state.update { it.copy(ratingPromptState = RatingPromptState.HIDDEN) }
        viewModelScope.launch {
            _effect.emit(HomeSideEffect.LaunchReviewFlow)
        }
    }

    private fun sendFeedback() {
        val currentState = state.value
        logRatingAnswered(RatingAction.FEEDBACK)
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

    private fun onNotificationConsentDismissed() {
        logNotificationPromptAnswered(NotificationPromptAction.DISMISSED)
        _state.update { it.copy(showNotificationConsentPrompt = false) }
    }

    /**
     * Ocena pochodzi z [handleRatingSelected]; przy odrzuceniu bez wyboru gwiazdek zostaje 0.
     *
     * Jedna odpowiedz na jedno pokazanie karty: wysylka feedbacku loguje sie w momencie tapniecia,
     * a przy bledzie sieci przycisk wraca do stanu aktywnego i uzytkownik moze sprobowac ponownie.
     */
    private fun logRatingAnswered(action: RatingAction) {
        if (hasLoggedRatingAnswer) return
        hasLoggedRatingAnswer = true
        analyticsLogger.log(
            AnalyticsEvent.RatingPromptAnswered(state.value.ratingValue, action)
        )
    }

    private fun logNotificationPromptAnswered(action: NotificationPromptAction) {
        analyticsLogger.log(AnalyticsEvent.NotificationPromptAnswered(action))
    }

    private fun neverAskAgain() {
        logRatingAnswered(RatingAction.NEVER_AGAIN)
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
        // Panel otwiera sie takze dla posiadanego trybu (pokazuje wtedy "Zacznij", nie oferte),
        // wiec bez tego warunku mianownik konwersji paywall -> zakup bylby zawyzony.
        if (!isModeUnlocked(modeId)) {
            analyticsLogger.log(
                AnalyticsEvent.PaywallShown(
                    surface = PurchaseSurface.HOME_SHEET,
                    productId = modeId,
                    hasPrice = productDetailsFor(modeId) != null,
                )
            )
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
        val details = productDetailsFor(modeId)
        if (details != null) {
            pendingPurchaseModeId = modeId
            _state.update { it.copy(isPurchasing = true, purchaseError = null) }
            purchaseFunnelTracker.onPurchaseStarted(PurchaseSurface.HOME_SHEET, details)
            billingRepository.launchBillingFlow(activity, details)
        } else {
            // Bez tego klikniecie "Kup" bez cen w cache jest cicha utrata przychodu:
            // metoda po prostu wychodzi, a UI nie pokazuje bledu.
            analyticsLogger.log(
                AnalyticsEvent.PaywallPriceMissing(PurchaseSurface.HOME_SHEET, modeId)
            )
        }
    }

    private fun isModeUnlocked(modeId: String): Boolean = when (modeId) {
        BillingIds.ID_TRANSLATION_MODE -> state.value.isTranslationModeUnlocked
        BillingIds.ID_SWIPE_MODE -> state.value.isSwipeModeUnlocked
        else -> false
    }

    private fun productDetailsFor(modeId: String): AppProduct? = when (modeId) {
        BillingIds.ID_TRANSLATION_MODE -> translationModeProductDetails
        BillingIds.ID_SWIPE_MODE -> swipeModeProductDetails
        else -> null
    }

    private fun consumeNavigation() {
    }
}

package com.rafalskrzypczyk.home_screen.presentation.store

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.core.api_response.ResponseState

@Immutable
data class StoreState(
    val responseState: ResponseState = ResponseState.Idle,
    val isPurchasing: Boolean = false,
    val purchaseError: String? = null,
    
    // Statusy odblokowania
    val isPremium: Boolean = false,
    val isTranslationModeUnlocked: Boolean = false,
    val isSwipeModeUnlocked: Boolean = false,
    val isCategoryUnlocked: Boolean = false,
    val isAdFreeUnlocked: Boolean = false,
    
    // Statusy przetwarzania
    val isPremiumPending: Boolean = false,
    val isTranslationModePending: Boolean = false,
    val isSwipeModePending: Boolean = false,
    val isCategoryPending: Boolean = false,
    val isAdFreePending: Boolean = false,
    
    // Ceny
    val fullPackageProduct: AppProduct? = null,
    val translationModeProduct: AppProduct? = null,
    val swipeModeProduct: AppProduct? = null,
    val categoryProduct: AppProduct? = null,
    val adFreeProduct: AppProduct? = null,

    // Postęp odblokowań (indywidualne pozycje, bez samego pakietu Premium)
    val unlockedItemsCount: Int = 0,
    val totalItemsCount: Int = 4,

    // Oszczędność na pakiecie Premium (sformatowana kwota, np. "4,97 zł"); null = brak/ukryj
    val savingsText: String? = null,

    // Liczba pytań w pakiecie dodatkowej kategorii
    val categoryQuestionCount: Int = 0,

    val pendingPurchaseModeId: String? = null
)

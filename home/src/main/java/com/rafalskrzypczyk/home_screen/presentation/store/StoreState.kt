package com.rafalskrzypczyk.home_screen.presentation.store

import androidx.compose.runtime.Immutable
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
    
    // Ceny
    val fullPackagePrice: String? = null,
    val translationModePrice: String? = null,
    val swipeModePrice: String? = null,
    val categoryPrice: String? = null,
    val adFreePrice: String? = null,
    
    val pendingPurchaseModeId: String? = null
)

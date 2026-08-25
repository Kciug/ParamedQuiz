package com.rafalskrzypczyk.billing.domain

import com.rafalskrzypczyk.core.error.AppError

sealed interface PurchaseResult {
    data class Success(val productId: String) : PurchaseResult
    data class Pending(val productId: String) : PurchaseResult
    object Cancelled : PurchaseResult
    data class Error(val error: AppError) : PurchaseResult
}

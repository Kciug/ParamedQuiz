package com.rafalskrzypczyk.billing.domain

sealed interface PurchaseResult {
    data class Success(val productId: String) : PurchaseResult
    object Cancelled : PurchaseResult
    data class Error(val message: String) : PurchaseResult
}

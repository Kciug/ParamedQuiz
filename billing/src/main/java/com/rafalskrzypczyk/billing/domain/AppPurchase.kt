package com.rafalskrzypczyk.billing.domain

data class AppPurchase(
    val products: List<String>,
    val isPurchased: Boolean,
    val purchaseToken: String = ""
)
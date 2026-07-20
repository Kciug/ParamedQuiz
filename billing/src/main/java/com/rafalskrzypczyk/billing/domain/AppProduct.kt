package com.rafalskrzypczyk.billing.domain

data class AppProduct(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val priceAmountMicros: Long = 0L,
    val priceCurrencyCode: String = ""
)

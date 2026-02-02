package com.rafalskrzypczyk.billing.domain

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val purchases: Flow<List<Purchase>>
    val availableProducts: Flow<List<ProductDetails>>
    val isBillingSetupFinished: Flow<Boolean>

    fun startBillingConnection()
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails)
    suspend fun queryProducts(productIds: List<String>)
    suspend fun consumePurchase(purchaseToken: String)
}
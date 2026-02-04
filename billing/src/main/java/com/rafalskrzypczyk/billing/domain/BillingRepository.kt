package com.rafalskrzypczyk.billing.domain

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val purchases: Flow<List<AppPurchase>>
    val availableProducts: Flow<List<AppProduct>>
    val isBillingSetupFinished: Flow<Boolean>

    fun startBillingConnection()
    fun launchBillingFlow(activity: Activity, product: AppProduct)
    suspend fun queryProducts(productIds: List<String>)
    suspend fun consumePurchase(purchaseToken: String)
}
package com.rafalskrzypczyk.billing.data

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.rafalskrzypczyk.billing.domain.BillingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingDataSource: BillingDataSource
) : BillingRepository {

    override val purchases: Flow<List<Purchase>> = billingDataSource.purchases
    override val availableProducts: Flow<List<ProductDetails>> = billingDataSource.availableProducts
    override val isBillingSetupFinished: Flow<Boolean> = billingDataSource.isBillingSetupFinished

    override fun startBillingConnection() {
        billingDataSource.startConnection()
    }

    override fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        billingDataSource.launchBillingFlow(activity, productDetails)
    }

    override suspend fun queryProducts(productIds: List<String>) {
        billingDataSource.queryProductDetails(productIds)
    }

    override suspend fun consumePurchase(purchaseToken: String) {
        billingDataSource.consumePurchase(purchaseToken)
    }
}
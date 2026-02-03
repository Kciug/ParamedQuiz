package com.rafalskrzypczyk.billing.data

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.AppPurchase
import com.rafalskrzypczyk.billing.domain.BillingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingDataSource: BillingDataSource
) : BillingRepository {

    private val productDetailsCache = mutableMapOf<String, ProductDetails>()

    override val purchases: Flow<List<AppPurchase>> = billingDataSource.purchases.map { list ->
        list.map { purchase ->
            AppPurchase(
                products = purchase.products,
                isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED,
                purchaseToken = purchase.purchaseToken
            )
        }
    }

    override val availableProducts: Flow<List<AppProduct>> = billingDataSource.availableProducts.map { list ->
        list.map { productDetails ->
            productDetailsCache[productDetails.productId] = productDetails
            AppProduct(
                id = productDetails.productId,
                name = productDetails.name,
                description = productDetails.description,
                price = productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
            )
        }
    }

    override val isBillingSetupFinished: Flow<Boolean> = billingDataSource.isBillingSetupFinished

    override fun startBillingConnection() {
        billingDataSource.startConnection()
    }

    override fun launchBillingFlow(activity: Activity, product: AppProduct) {
        val details = productDetailsCache[product.id]
        if (details != null) {
            billingDataSource.launchBillingFlow(activity, details)
        }
    }

    override suspend fun queryProducts(productIds: List<String>) {
        billingDataSource.queryProductDetails(productIds)
    }

    override suspend fun consumePurchase(purchaseToken: String) {
        billingDataSource.consumePurchase(purchaseToken)
    }
}
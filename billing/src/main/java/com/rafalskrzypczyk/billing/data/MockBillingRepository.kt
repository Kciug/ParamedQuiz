package com.rafalskrzypczyk.billing.data

import android.app.Activity
import com.rafalskrzypczyk.billing.domain.AppProduct
import com.rafalskrzypczyk.billing.domain.AppPurchase
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockBillingRepository @Inject constructor() : BillingRepository {

    private val _purchases = MutableStateFlow<List<AppPurchase>>(emptyList())
    override val purchases: Flow<List<AppPurchase>> = _purchases.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<AppProduct>>(emptyList())
    override val availableProducts: Flow<List<AppProduct>> = _availableProducts.asStateFlow()

    private val _isBillingSetupFinished = MutableStateFlow(true)
    override val isBillingSetupFinished: Flow<Boolean> = _isBillingSetupFinished.asStateFlow()

    override fun startBillingConnection() {
        _isBillingSetupFinished.value = true
    }

    override fun launchBillingFlow(activity: Activity, product: AppProduct) {
        // Simulate immediate successful purchase with delay
        val scope = kotlinx.coroutines.MainScope()
        scope.launch {
            kotlinx.coroutines.delay(1000)
            val newPurchase = AppPurchase(
                products = listOf(product.id),
                isPurchased = true,
                purchaseToken = "mock_token_${System.currentTimeMillis()}"
            )
            _purchases.update { it + newPurchase }
        }
    }

    override suspend fun queryProducts(productIds: List<String>) {
        val mockProducts = productIds.map { id ->
            val (name, price) = when (id) {
                BillingIds.ID_FULL_PACKAGE -> "Full Package" to "29.99 zł"
                BillingIds.ID_TRANSLATION_MODE -> "Translation Mode" to "9.99 zł"
                BillingIds.ID_SWIPE_MODE -> "Swipe Mode" to "14.99 zł"
                else -> "Product $id" to "0.00 zł"
            }
            AppProduct(
                id = id,
                name = name,
                description = "Mock description for $name",
                price = price
            )
        }
        _availableProducts.update { current ->
            (current + mockProducts).distinctBy { it.id }
        }
    }

    override suspend fun consumePurchase(purchaseToken: String) {
        _purchases.update { list -> list.filter { it.purchaseToken != purchaseToken } }
    }
}

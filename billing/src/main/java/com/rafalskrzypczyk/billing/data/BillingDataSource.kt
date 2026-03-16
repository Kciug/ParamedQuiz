package com.rafalskrzypczyk.billing.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.rafalskrzypczyk.billing.domain.PurchaseResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BillingDataSource"

@Singleton
class BillingDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val externalScope: CoroutineScope,
    private val billingClientProvider: BillingClientProvider,
    private val billingError: BillingError
) : PurchasesUpdatedListener, BillingClientStateListener {

    private val _isBillingSetupFinished = MutableStateFlow(false)
    val isBillingSetupFinished: StateFlow<Boolean> = _isBillingSetupFinished.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val availableProducts: StateFlow<List<ProductDetails>> = _availableProducts.asStateFlow()

    private val _purchases = MutableStateFlow<List<Purchase>>(emptyList())
    val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()

    private val _purchaseResult = MutableSharedFlow<PurchaseResult>()
    val purchaseResult: SharedFlow<PurchaseResult> = _purchaseResult.asSharedFlow()

    private val billingClient: BillingClient by lazy {
        billingClientProvider.create(context, this)
    }

    fun startConnection() {
        if (billingClient.isReady) {
            _isBillingSetupFinished.value = true
            return
        }
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Billing setup finished")
            _isBillingSetupFinished.value = true
            refreshPurchases()
        } else {
            Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
            _isBillingSetupFinished.value = false
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "Billing service disconnected")
        _isBillingSetupFinished.value = false
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases, isFullRefresh = false)
            externalScope.launch {
                purchases.forEach { purchase ->
                    purchase.products.forEach { productId ->
                        when (purchase.purchaseState) {
                            Purchase.PurchaseState.PURCHASED -> {
                                _purchaseResult.emit(PurchaseResult.Success(productId))
                            }
                            Purchase.PurchaseState.PENDING -> {
                                _purchaseResult.emit(PurchaseResult.Pending(productId))
                            }
                        }
                    }
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "User canceled purchase")
            externalScope.launch {
                _purchaseResult.emit(PurchaseResult.Cancelled)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            refreshPurchases()
            externalScope.launch {
                _purchaseResult.emit(PurchaseResult.Error(billingError.localizedError(billingResult.responseCode)))
            }
        } else {
            Log.e(TAG, "Purchase update failed: ${billingResult.debugMessage} (code: ${billingResult.responseCode})")
            externalScope.launch {
                _purchaseResult.emit(PurchaseResult.Error(billingError.localizedError(billingResult.responseCode)))
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        val result = billingClient.launchBillingFlow(activity, flowParams)
        if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            refreshPurchases()
        } else if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            externalScope.launch {
                _purchaseResult.emit(PurchaseResult.Error(billingError.localizedError(result.responseCode)))
            }
        }
    }

    suspend fun queryProductDetails(productIds: List<String>) {
        if (productIds.isEmpty()) return

        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }

        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _availableProducts.value = result.productDetailsList ?: emptyList()
        } else {
            Log.e(TAG, "Query product details failed: ${result.billingResult.debugMessage}")
        }
    }

    fun refreshPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchasesList, isFullRefresh = true)
            } else {
                Log.e(TAG, "Query purchases failed: ${billingResult.debugMessage}")
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>, isFullRefresh: Boolean) {
        externalScope.launch {
            val validPurchases = purchases.filter { purchase ->
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED ||
                        purchase.purchaseState == Purchase.PurchaseState.PENDING
            }

            validPurchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            }

            if (isFullRefresh) {
                _purchases.value = validPurchases
            } else {
                _purchases.update { currentPurchases ->
                    // distinctBy keeps the first element it encounters. 
                    // Putting validPurchases first ensures that updated states (like PURCHASED) overwrite old ones (like PENDING).
                    (validPurchases + currentPurchases).distinctBy { it.purchaseToken }
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = withContext(Dispatchers.IO) {
            billingClient.acknowledgePurchase(params)
        }

        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Acknowledge purchase failed: ${result.debugMessage}")
        }
    }

    suspend fun consumePurchase(purchaseToken: String) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        withContext(Dispatchers.IO) {
            billingClient.consumePurchase(params)
        }
    }
}
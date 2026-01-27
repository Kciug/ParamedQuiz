package com.rafalskrzypczyk.billing.data

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener
import javax.inject.Inject

open class BillingClientProvider @Inject constructor() {
    open fun create(context: Context, listener: PurchasesUpdatedListener): BillingClient {
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        return BillingClient.newBuilder(context)
            .setListener(listener)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()
    }
}
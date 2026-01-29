package com.rafalskrzypczyk.billing.data

import com.android.billingclient.api.Purchase
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingPremiumStatusProvider @Inject constructor(
    private val billingRepository: BillingRepository
) : PremiumStatusProvider {

    // Any purchase removes ads
    override val isAdsFree: Flow<Boolean> = billingRepository.purchases
        .map { purchaseList ->
            purchaseList.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        }

    override fun hasAccessTo(contentId: String): Flow<Boolean> = billingRepository.purchases
        .map { purchaseList ->
            val ownedProductIds = purchaseList
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .flatMap { it.products } // Purchase.products is a list of Strings (SKUs)

            // Access granted if user owns the Full Package OR the specific content ID
            ownedProductIds.contains(BillingIds.ID_FULL_PACKAGE) || ownedProductIds.contains(contentId)
        }
}

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

    override val ownedProductIds: Flow<Set<String>> = billingRepository.purchases
        .map { purchaseList ->
            purchaseList
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .flatMap { it.products }
                .toSet()
        }

    override fun hasAccessTo(contentId: String): Flow<Boolean> = ownedProductIds
        .map { ownedIds ->
            ownedIds.contains(BillingIds.ID_FULL_PACKAGE) || ownedIds.contains(contentId)
        }
}

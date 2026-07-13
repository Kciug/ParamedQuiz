package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake statusu premium. Domyślnie brak zakupów (użytkownik bez premium, reklamy aktywne).
 * Testy sterują dostępem przez [setOwned] / [setPending].
 */
class FakePremiumStatusProvider : PremiumStatusProvider {

    private val owned = MutableStateFlow<Set<String>>(emptySet())
    private val pending = MutableStateFlow<Set<String>>(emptySet())

    override val ownedProductIds: Flow<Set<String>> = owned
    override val pendingProductIds: Flow<Set<String>> = pending

    override val isAdsFree: Flow<Boolean> = owned.map { ids ->
        ids.contains(BillingIds.ID_FULL_PACKAGE) || ids.contains(BillingIds.ID_AD_FREE)
    }

    override fun hasAccessTo(contentId: String): Flow<Boolean> = owned.map { ids ->
        ids.contains(BillingIds.ID_FULL_PACKAGE) || ids.contains(contentId)
    }

    fun setOwned(vararg productIds: String) {
        owned.value = productIds.toSet()
    }

    fun setPending(vararg productIds: String) {
        pending.value = productIds.toSet()
    }
}

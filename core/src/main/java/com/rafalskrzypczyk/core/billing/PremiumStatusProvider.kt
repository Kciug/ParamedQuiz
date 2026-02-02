package com.rafalskrzypczyk.core.billing

import kotlinx.coroutines.flow.Flow

interface PremiumStatusProvider {
    val isAdsFree: Flow<Boolean>
    val ownedProductIds: Flow<Set<String>>
    fun hasAccessTo(contentId: String): Flow<Boolean>
}

package com.rafalskrzypczyk.core.billing

import kotlinx.coroutines.flow.Flow

interface PremiumStatusProvider {
    val isAdsFree: Flow<Boolean>
    fun hasAccessTo(contentId: String): Flow<Boolean>
}

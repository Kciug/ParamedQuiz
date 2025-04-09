package com.rafalskrzypczyk.firestore.domain.models

data class UserPurchasesDTO(
    val userId: Long = -1,
    val unlockedCategories: List<Long> = emptyList()
)

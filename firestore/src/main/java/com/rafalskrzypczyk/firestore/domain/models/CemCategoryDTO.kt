package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class CemCategoryDTO(
    val id: Long = 0,
    val parentCategoryID: Long? = null,
    val title: String = "",
    val subtitle: String? = null,
    val questionsCount: Long = 0,
    val subcategoriesCount: Long? = null,
    val subcategoryIDs: List<Long> = emptyList(),
    val questionIDs: List<Long> = emptyList(),
    val free: Boolean? = false
)

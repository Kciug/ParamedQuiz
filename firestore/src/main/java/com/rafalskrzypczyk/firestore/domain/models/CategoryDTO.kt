package com.rafalskrzypczyk.firestore.domain.models

data class CategoryDTO(
    val id: Long = -1,
    val title: String = "",
    val subtitle: String? = null,
    val questionCount: Int = 0,
)

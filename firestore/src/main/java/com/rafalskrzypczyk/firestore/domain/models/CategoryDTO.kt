package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class CategoryDTO(
    val id: Long = -1,
    val title: String = "",
    val subtitle: String = "",
    val questionsCount: Int = 0,
    val free: Boolean = false,
)

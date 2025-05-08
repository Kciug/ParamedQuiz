package com.rafalskrzypczyk.main_mode.domain.models

import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO

data class Category (
    val id: Long = -1,
    val title: String = "",
    val subtitle: String? = null,
    val questionCount: Int = 0,
)

fun CategoryDTO.toDomain() : Category = Category(
    id = id,
    title = title,
    subtitle = subtitle,
    questionCount = questionCount
)
package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import com.rafalskrzypczyk.main_mode.domain.models.Category

data class CategoryUIM(
    val id: Long,
    val title: String,
    val subtitle: String? = null,
    val questionCount: Int,
)

fun Category.toUIM() : CategoryUIM = CategoryUIM(
    id = id,
    title = title,
    subtitle = subtitle,
    questionCount = questionCount
)

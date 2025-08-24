package com.rafalskrzypczyk.main_mode.domain.models

import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO

data class Category (
    val id: Long,
    val title: String,
    val subtitle: String,
    val questionsCount: Int,
    val unlocked: Boolean
)

fun CategoryDTO.toDomain() : Category = Category(
    id = id,
    title = title,
    subtitle = subtitle,
    questionsCount = questionsCount,
    unlocked = free
)
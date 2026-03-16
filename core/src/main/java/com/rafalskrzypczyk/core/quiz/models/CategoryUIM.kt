package com.rafalskrzypczyk.core.quiz.models

import androidx.compose.runtime.Immutable

@Immutable
data class CategoryUIM(
    val id: Long,
    val title: String,
    val description: String,
    val questionCount: String,
    val subcategoriesCount: Long = 0,
    val unlocked: Boolean = true,
    val isPending: Boolean = false,
    val progress: Float = 0f,
    val isPremium: Boolean = false,
    val questionIDs: List<Long> = emptyList(),
)

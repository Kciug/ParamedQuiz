package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.main_mode.domain.models.Category

@Immutable
data class CategoryUIM(
    val id: Long,
    val title: String,
    val description: String,
    val questionCount: String,
    val unlocked: Boolean = true,
    val progress: Float,
)

fun Category.toUIM(progress: Float = 0f) : CategoryUIM = CategoryUIM(
    id = id,
    title = title,
    description = subtitle,
    questionCount = questionsCount.toString(),
    unlocked = unlocked,
    progress = progress
)

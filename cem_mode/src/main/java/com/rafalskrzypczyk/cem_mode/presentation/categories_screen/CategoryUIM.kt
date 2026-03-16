package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory

fun CemCategory.toUIM(hasAccess: Boolean = unlocked, isPending: Boolean = false): CategoryUIM = CategoryUIM(
    id = id,
    title = title,
    description = subtitle,
    questionCount = questionsCount.toString(),
    subcategoriesCount = subcategoriesCount,
    unlocked = hasAccess,
    isPending = isPending,
    progress = 0f,
    questionIDs = questionIDs
)

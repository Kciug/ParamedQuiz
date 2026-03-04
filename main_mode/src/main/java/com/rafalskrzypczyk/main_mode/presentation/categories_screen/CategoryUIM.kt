package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.main_mode.domain.models.Category

fun Category.toUIM(progress: Float = 0f) : CategoryUIM = CategoryUIM(
    id = id,
    title = title,
    description = subtitle,
    questionCount = questionsCount.toString(),
    unlocked = unlocked,
    progress = progress
)

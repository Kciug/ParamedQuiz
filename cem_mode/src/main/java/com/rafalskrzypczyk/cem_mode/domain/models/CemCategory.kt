package com.rafalskrzypczyk.cem_mode.domain.models

import com.rafalskrzypczyk.firestore.domain.models.CemCategoryDTO

data class CemCategory(
    val id: Long,
    val parentCategoryID: Long,
    val title: String,
    val subtitle: String,
    val questionsCount: Long,
    val subcategoriesCount: Long,
    val subcategoryIDs: List<Long>,
    val questionIDs: List<Long>,
    val unlocked: Boolean
)

fun CemCategoryDTO.toDomain(): CemCategory = CemCategory(
    id = id,
    parentCategoryID = parentCategoryID ?: 0,
    title = title,
    subtitle = subtitle ?: "",
    questionsCount = questionsCount,
    subcategoriesCount = subcategoriesCount ?: 0,
    subcategoryIDs = subcategoryIDs,
    questionIDs = questionIDs,
    unlocked = free ?: false
)

package com.rafalskrzypczyk.cem_mode.navigation

import kotlinx.serialization.Serializable

@Serializable
data class CemCategoriesRoute(
    val parentId: Long = 0,
    val categoryTitle: String = ""
)

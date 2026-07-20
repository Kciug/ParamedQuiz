package com.rafalskrzypczyk.home_screen.presentation.store

import androidx.compose.runtime.Immutable

/**
 * Pojedynczy płatny pakiet pytań (kategoria) prezentowany w sklepie.
 * Budowany dynamicznie z kategorii Firestore (free=false) + cen z Google Play.
 */
@Immutable
data class StoreCategoryPack(
    val id: Long,
    val sku: String,
    val title: String,
    val description: String,
    val questionCount: Int,
    val price: String?,
    val isUnlocked: Boolean,
    val isPending: Boolean
)

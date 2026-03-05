package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class NewsBannerDTO(
    val id: String = "",
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    @field:JvmField
    val isActive: Boolean = false
)

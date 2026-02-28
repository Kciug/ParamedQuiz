package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class FeedbackDTO(
    val id: String = "",
    val userId: String? = null,
    val feedback: String = "",
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

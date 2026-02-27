package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class SwipeQuestionDTO(
    val id: Long = -1,
    val text: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
    @field:JvmField
    val isFree: Boolean = false
)

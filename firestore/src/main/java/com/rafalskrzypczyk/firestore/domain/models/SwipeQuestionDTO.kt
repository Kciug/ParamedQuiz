package com.rafalskrzypczyk.firestore.domain.models

data class SwipeQuestionDTO(
    val id: Long = -1,
    val text: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
)

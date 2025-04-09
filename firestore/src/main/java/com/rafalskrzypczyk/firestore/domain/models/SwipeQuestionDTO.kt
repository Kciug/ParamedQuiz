package com.rafalskrzypczyk.firestore.domain.models

import java.util.Date

data class SwipeQuestionDTO(
    val id: Long = -1,
    val text: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
)

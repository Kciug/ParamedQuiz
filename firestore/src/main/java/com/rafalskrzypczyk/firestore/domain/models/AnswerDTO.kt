package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class AnswerDTO(
    val id: Long = -1,
    val answerText: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
)

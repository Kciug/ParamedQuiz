package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class QuestionDTO(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<AnswerDTO> = emptyList(),
    val categoryIDs: List<Long> = emptyList()
)

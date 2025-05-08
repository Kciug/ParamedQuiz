package com.rafalskrzypczyk.firestore.domain.models

data class QuestionDTO(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<AnswerDTO> = emptyList(),
    val categoryIDs: List<Long> = emptyList()
)

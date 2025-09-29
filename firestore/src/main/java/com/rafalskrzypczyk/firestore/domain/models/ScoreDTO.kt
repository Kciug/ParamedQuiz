package com.rafalskrzypczyk.firestore.domain.models

data class ScoreDTO(
    val score: Long = 0,
    val streak: Long = 0,
    val seenQuestions: List<QuestionAnnotationDTO> = emptyList()
)

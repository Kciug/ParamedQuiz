package com.rafalskrzypczyk.main_mode.domain.models

import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO

data class Question(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<Answer> = emptyList(),
    val assignedCategoriesIds: List<Long> = emptyList(),
)


fun QuestionDTO.toDomain() : Question = Question(
    id = id,
    questionText = questionText,
    answers = answers.map { it.toDomain() },
    assignedCategoriesIds = categoryIDs
)
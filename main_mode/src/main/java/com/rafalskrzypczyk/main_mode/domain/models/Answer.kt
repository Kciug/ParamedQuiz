package com.rafalskrzypczyk.main_mode.domain.models

import com.rafalskrzypczyk.firestore.domain.models.AnswerDTO

data class Answer(
    val id: Long = -1,
    val answerText: String = "",
    val isCorrect: Boolean = false,
)

fun AnswerDTO.toDomain() : Answer = Answer(
    id = id,
    answerText = answerText,
    isCorrect = isCorrect
)
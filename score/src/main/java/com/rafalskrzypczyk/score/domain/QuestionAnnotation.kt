package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.firestore.domain.models.QuestionAnnotationDTO

data class QuestionAnnotation(
    val questionId: Long,
    val timesSeen: Long,
    val timesCorrect: Long,
)

fun QuestionAnnotationDTO.toDomain() = QuestionAnnotation(
    questionId = questionId,
    timesSeen = timesSeen,
    timesCorrect = timesCorrect
)

fun QuestionAnnotation.toDTO() = QuestionAnnotationDTO(
    questionId = questionId,
    timesSeen = timesSeen,
    timesCorrect = timesCorrect
)

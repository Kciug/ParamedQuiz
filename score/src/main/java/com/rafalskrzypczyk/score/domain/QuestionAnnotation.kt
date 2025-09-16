package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.firestore.domain.models.QuestionAnnotationDTO
import kotlinx.serialization.Serializable

@Serializable
data class QuestionAnnotation(
    val questionId: Long,
    val timesSeen: Long,
    val timesCorrect: Long,
    val timesIncorrect: Long = timesSeen - timesCorrect
) {
    companion object {
        fun new(questionId: Long, answeredCorrectly: Boolean): QuestionAnnotation =
            QuestionAnnotation(
                questionId = questionId,
                timesSeen = 1,
                timesCorrect = if (answeredCorrectly) 1 else 0
            )
    }
}

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

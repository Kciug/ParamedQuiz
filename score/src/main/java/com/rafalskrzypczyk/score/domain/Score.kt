package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.firestore.domain.models.ScoreDTO
import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val score: Long,
    val streak: Long,
    val seenQuestions: List<QuestionAnnotation>
) {
    companion object {
        fun empty() = Score(0, 0, emptyList())
    }
}

fun Score.isEmpty(): Boolean {
    return score == 0L && seenQuestions.isEmpty()
}

fun ScoreDTO.toDomain() = Score(
    score = score,
    streak = streak,
    seenQuestions = seenQuestions.map { it.toDomain() }
)

fun Score.toDTO() = ScoreDTO(
    score = score,
    streak = streak,
    seenQuestions = seenQuestions.map { it.toDTO() }
)

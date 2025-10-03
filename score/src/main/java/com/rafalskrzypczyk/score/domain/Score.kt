package com.rafalskrzypczyk.score.domain

import com.google.firebase.Timestamp
import com.rafalskrzypczyk.firestore.domain.models.ScoreDTO
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Score(
    val score: Int,
    val streak: Int,
    @Contextual val lastStreakUpdateDate: Date?,
    @Contextual val lastDailyExerciseDate: Date?,
    val seenQuestions: List<QuestionAnnotation>
) {
    companion object {
        fun empty() = Score(0, 0, null, null, emptyList())
    }
}

fun Score.isEmpty(): Boolean {
    return score == 0 && seenQuestions.isEmpty()
}

fun ScoreDTO.toDomain() = Score(
    score = score.toInt(),
    streak = streak.toInt(),
    lastStreakUpdateDate = lastStreakUpdateDate?.toDate(),
    lastDailyExerciseDate = lastDailyExerciseDate?.toDate(),
    seenQuestions = seenQuestions.map { it.toDomain() }
)

fun Score.toDTO() = ScoreDTO(
    score = score.toLong(),
    streak = streak.toLong(),
    lastStreakUpdateDate = lastStreakUpdateDate?.let { Timestamp(it) },
    lastDailyExerciseDate = lastDailyExerciseDate?.let { Timestamp(it) },
    seenQuestions = seenQuestions.map { it.toDTO() }
)

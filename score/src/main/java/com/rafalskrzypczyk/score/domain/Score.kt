package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.firestore.domain.models.ScoreDTO

data class Score(
    val score: Long,
    val seenQuestions: List<QuestionAnnotation>
)

fun ScoreDTO.toDomain() = Score(
    score = score,
    seenQuestions = seenQuestions.map { it.toDomain() }
)

fun Score.toDTO() = ScoreDTO(
    score = score,
    seenQuestions = seenQuestions.map { it.toDTO() }
)

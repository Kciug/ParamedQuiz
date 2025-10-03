package com.rafalskrzypczyk.firestore.domain.models

import com.google.firebase.Timestamp

data class ScoreDTO(
    val score: Long = 0,
    val streak: Long = 0,
    val lastStreakUpdateDate: Timestamp? = null,
    val lastDailyExerciseDate: Timestamp? = null,
    val seenQuestions: List<QuestionAnnotationDTO> = emptyList()
)

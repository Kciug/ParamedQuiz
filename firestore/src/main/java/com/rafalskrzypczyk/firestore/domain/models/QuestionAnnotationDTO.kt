package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class QuestionAnnotationDTO(
    val questionId: Long = 0,
    val timesSeen: Long = 0,
    val timesCorrect: Long = 0,
)

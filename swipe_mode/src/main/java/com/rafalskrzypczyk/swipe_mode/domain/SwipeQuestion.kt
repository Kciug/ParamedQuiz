package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.firestore.domain.models.SwipeQuestionDTO

data class SwipeQuestion(
    val id: Long,
    val text: String,
    val isCorrect: Boolean,
    val isFree: Boolean
)

fun SwipeQuestionDTO.toDomain() = SwipeQuestion(
    id = id,
    text = text,
    isCorrect = isCorrect,
    isFree = isFree
)

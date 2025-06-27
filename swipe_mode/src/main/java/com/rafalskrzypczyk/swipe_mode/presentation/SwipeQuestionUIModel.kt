package com.rafalskrzypczyk.swipe_mode.presentation

import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion

data class SwipeQuestionUIModel(
    val id: Long,
    val text: String
)

fun SwipeQuestion.toPresentation() = SwipeQuestionUIModel(
    id = id,
    text = text
)

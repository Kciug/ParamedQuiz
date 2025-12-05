package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion

@Immutable
data class SwipeQuestionUIModel(
    val id: Long,
    val text: String
)

fun SwipeQuestion.toPresentation() = SwipeQuestionUIModel(
    id = id,
    text = text
)

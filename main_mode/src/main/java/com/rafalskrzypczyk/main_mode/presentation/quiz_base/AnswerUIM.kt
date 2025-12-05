package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.main_mode.domain.models.Answer

@Immutable
data class AnswerUIM(
    val id: Long = -1,
    val answerText: String = "",
    val isSelected: Boolean = false,
)

fun Answer.toUIM() : AnswerUIM = AnswerUIM(
    id = id,
    answerText = answerText,
    isSelected = false
)

fun AnswerUIM.makeSelected() : AnswerUIM = copy(isSelected = true)

fun AnswerUIM.makeDeselected() : AnswerUIM = copy(isSelected = false)
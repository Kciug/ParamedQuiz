package com.rafalskrzypczyk.home_screen.domain.models

import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion

data class SimpleQuestion(
    val id: Long,
    val questionText: String,
)

fun Question.toSimpleQuestion() = SimpleQuestion(
    id = id,
    questionText = questionText
)

fun SwipeQuestion.toSimpleQuestion() = SimpleQuestion(
    id = id,
    questionText = text
)

fun TranslationQuestionDTO.toSimpleQuestion() = SimpleQuestion(
    id = id,
    questionText = phrase
)

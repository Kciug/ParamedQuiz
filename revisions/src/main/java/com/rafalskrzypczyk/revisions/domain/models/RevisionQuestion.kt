package com.rafalskrzypczyk.revisions.domain.models

import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.main_mode.domain.models.Question

sealed interface RevisionQuestion {
    val id: Long
    val text: String

    data class Main(
        val question: Question
    ) : RevisionQuestion {
        override val id: Long get() = question.id
        override val text: String get() = question.questionText
    }

    data class Cem(
        val question: Question
    ) : RevisionQuestion {
        override val id: Long get() = question.id
        override val text: String get() = question.questionText
    }

    data class Translation(
        val question: TranslationQuestionDTO
    ) : RevisionQuestion {
        override val id: Long get() = question.id
        override val text: String get() = question.phrase
    }
}

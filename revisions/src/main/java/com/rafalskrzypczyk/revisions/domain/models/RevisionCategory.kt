package com.rafalskrzypczyk.revisions.domain.models

import com.rafalskrzypczyk.core.utils.QuizMode

data class RevisionCategory(
    val id: Long,
    val title: String,
    val mode: QuizMode,
    val totalQuestionsCount: Int,
    val answeredQuestionsCount: Int,
    val isEligible: Boolean
)

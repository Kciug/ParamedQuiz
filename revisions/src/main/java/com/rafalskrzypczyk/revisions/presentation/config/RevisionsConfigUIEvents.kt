package com.rafalskrzypczyk.revisions.presentation.config

import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion

sealed interface RevisionsConfigUIEvents {
    data class SelectMode(val mode: QuizMode) : RevisionsConfigUIEvents
    data class SelectCategory(val category: RevisionCategory) : RevisionsConfigUIEvents
    data class SelectCriterion(val criterion: RevisionCriterion) : RevisionsConfigUIEvents
    data class SelectLimit(val limit: Int?) : RevisionsConfigUIEvents
    data object DismissConfigDialog : RevisionsConfigUIEvents
    data object ShowCategoryDialog : RevisionsConfigUIEvents
    data object DismissCategoryDialog : RevisionsConfigUIEvents
}

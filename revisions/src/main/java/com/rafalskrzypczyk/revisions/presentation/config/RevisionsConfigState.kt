package com.rafalskrzypczyk.revisions.presentation.config

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion

@Immutable
data class RevisionsConfigState(
    val selectedMode: QuizMode = QuizMode.MainMode,
    val categoriesList: List<RevisionCategory> = emptyList(),
    val selectedCategory: RevisionCategory? = null,
    val selectedCriterion: RevisionCriterion = RevisionCriterion.WORST,
    val selectedLimit: Int? = 10,
    val availableQuestionsCount: Int = 0,
    val availableLimits: List<Int?> = emptyList(),
    val responseState: ResponseState = ResponseState.Idle,
    val isModeEligible: Boolean = true,
    val isEmptyState: Boolean = false,
    val isCategoriesLoading: Boolean = false,
    val isQuestionsLoading: Boolean = false
)

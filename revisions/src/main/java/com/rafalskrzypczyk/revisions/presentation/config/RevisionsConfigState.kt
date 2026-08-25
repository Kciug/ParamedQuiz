package com.rafalskrzypczyk.revisions.presentation.config

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.RevisionsConfig
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion

@Immutable
data class RevisionsConfigState(
    val selectedMode: QuizMode = QuizMode.MainMode,
    val categoriesList: List<RevisionCategory> = emptyList(),
    val selectedCategory: RevisionCategory? = null,
    val selectedCriterion: RevisionCriterion = RevisionCriterion.WORST,
    val selectedLimit: Int? = RevisionsConfig.DEFAULT_SELECTED_LIMIT,
    /**
     * Limit zadeklarowany przez uzytkownika, niezalezny od aktualnie dostepnych opcji.
     * [selectedLimit] jest z niego wyliczany przy kazdym przeliczeniu puli - dzieki temu wybor
     * wraca, gdy pula znow na niego pozwala (np. po wyjsciu ze stanu pustego).
     */
    val preferredLimit: Int? = RevisionsConfig.DEFAULT_SELECTED_LIMIT,
    val availableQuestionsCount: Int = 0,
    val availableLimits: List<Int?> = emptyList(),
    val responseState: ResponseState = ResponseState.Idle,
    val isModeEligible: Boolean = true,
    val isEmptyState: Boolean = false,
    val isCategoriesLoading: Boolean = false,
    val isQuestionsLoading: Boolean = false,
    /**
     * Trwa przeliczanie puli pytan. Swiadomie odseparowane od [isQuestionsLoading] - nie zapala
     * loadera (mrugalby przy kazdej zmianie chipa), a jedynie blokuje start sesji, zeby nie dalo
     * sie jej odpalic na nieaktualnym, jeszcze niepustym stanie.
     */
    val isRecalculatingPool: Boolean = false,
    val isConfigDialogVisible: Boolean = false,
    val isCategoryDialogVisible: Boolean = false,
    val loadingMode: QuizMode? = null
)

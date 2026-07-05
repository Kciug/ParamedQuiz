package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigState
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigUIEvents

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RevisionsConfigScreenContent(
    state: RevisionsConfigState,
    onEvent: (RevisionsConfigUIEvents) -> Unit,
    onTriggerCategoryDialog: () -> Unit,
    onStartSession: (QuizMode, Long?, RevisionCriterion, Int?) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            TextHeadline(
                text = stringResource(R.string.revisions_select_mode)
            )

            listOf(QuizMode.MainMode, QuizMode.CemMode, QuizMode.TranslationMode).forEach { mode ->
                val isSelected = state.selectedMode == mode
                val isEligible = if (mode == QuizMode.TranslationMode) {
                    state.isModeEligible
                } else {
                    true
                }

                ModeSelectorCard(
                    mode = mode,
                    selected = isSelected,
                    enabled = isEligible,
                    onClick = { onEvent(RevisionsConfigUIEvents.SelectMode(mode)) }
                )
            }

            if (state.selectedMode != QuizMode.TranslationMode) {
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                TextHeadline(
                    text = stringResource(R.string.revisions_select_pool)
                )
                if (state.isCategoriesLoading) {
                    CategorySelectionLoadingCard()
                } else if (state.categoriesList.isNotEmpty()) {
                    CategorySelectionTriggerCard(
                        category = state.selectedCategory,
                        onClick = onTriggerCategoryDialog
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            TextHeadline(
                text = stringResource(R.string.revisions_select_criterion)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                modifier = Modifier.fillMaxWidth()
            ) {
                RevisionCriterion.entries.forEach { criterion ->
                    val isSelected = state.selectedCriterion == criterion
                    RevisionsChoiceChip(
                        selected = isSelected,
                        title = when (criterion) {
                            RevisionCriterion.WORST -> "Najgorsze"
                            RevisionCriterion.BEST -> "Najlepsze"
                            RevisionCriterion.UNDER_50 -> "Poniżej 50% trafności"
                        },
                        onClick = { onEvent(RevisionsConfigUIEvents.SelectCriterion(criterion)) }
                    )
                }
            }

            if (!state.isEmptyState) {
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                TextHeadline(
                    text = stringResource(R.string.revisions_select_limit)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.availableLimits.forEach { limit ->
                        val isSelected = state.selectedLimit == limit
                        val title =
                            limit?.toString() ?: stringResource(R.string.revisions_limit_all, state.availableQuestionsCount)
                        RevisionsChoiceChip(
                            selected = isSelected,
                            title = title,
                            onClick = { onEvent(RevisionsConfigUIEvents.SelectLimit(limit)) }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(top = Dimens.LARGE_PADDING)
        ) {
            if (state.isEmptyState) {
                EmptyStateCard()
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            }

            val isStartEnabled = !state.isEmptyState && state.isModeEligible &&
                    (state.selectedMode == QuizMode.TranslationMode || (state.selectedCategory != null && state.selectedCategory.isEligible))

            ButtonPrimary(
                title = stringResource(R.string.revisions_start_btn),
                onClick = {
                    onStartSession(
                        state.selectedMode,
                        state.selectedCategory?.id,
                        state.selectedCriterion,
                        state.selectedLimit
                    )
                },
                enabled = isStartEnabled && !state.isQuestionsLoading,
                loading = state.isQuestionsLoading
            )
        }
    }
}

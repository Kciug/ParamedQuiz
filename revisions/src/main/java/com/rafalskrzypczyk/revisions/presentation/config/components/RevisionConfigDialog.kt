package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.BaseCustomDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigState
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigUIEvents

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RevisionConfigDialog(
    state: RevisionsConfigState,
    onEvent: (RevisionsConfigUIEvents) -> Unit,
    onTriggerCategoryDialog: () -> Unit,
    onStartSession: (QuizMode, Long?, RevisionCriterion, Int?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = state.isQuestionsLoading || state.isCategoriesLoading

    val isEmptyContent = !state.isModeEligible || state.isEmptyState

    val title = when (state.selectedMode) {
        QuizMode.MainMode -> stringResource(R.string.revisions_mode_main)
        QuizMode.CemMode -> stringResource(R.string.revisions_mode_cem)
        QuizMode.TranslationMode -> stringResource(R.string.revisions_mode_translation)
        else -> ""
    }

    val icon = ModeInfoProvider.getIcon(state.selectedMode)
    val headerColor = ModeInfoProvider.getColor(state.selectedMode)

    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = icon,
        title = title,
        headerColor = headerColor,
        headerContentColor = MaterialTheme.colorScheme.onPrimary,
        content = {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                if (isEmptyContent) {
                    val emptyTitle = if (!state.isModeEligible) {
                        "Tryb niedostępny"
                    } else {
                        stringResource(R.string.revisions_empty_state_title)
                    }

                    val msg = if (!state.isModeEligible) {
                        stringResource(R.string.revisions_mode_not_enough_answers)
                    } else {
                        stringResource(R.string.revisions_empty_state_msg)
                    }

                    EmptyStateCard(
                        title = emptyTitle,
                        message = msg
                    )
                } else {
                    if (state.selectedMode != QuizMode.TranslationMode) {
                        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                        TextHeadline(
                            text = stringResource(R.string.revisions_select_pool)
                        )
                        CategorySelectionTriggerCard(
                            category = state.selectedCategory,
                            onClick = onTriggerCategoryDialog
                        )
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

                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    TextHeadline(
                        text = stringResource(R.string.revisions_select_limit)
                    )

                    // Sekcja limitów odświeża się po zmianie kryterium/kategorii — trzymamy
                    // stabilną wysokość i lokalny loader, żeby dialog nie skakał.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.isQuestionsLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Loading()
                            }
                        } else {
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
                }
            }
        },
        buttons = {
            val isStartEnabled = !isLoading && !state.isEmptyState && state.isModeEligible &&
                    (state.selectedMode == QuizMode.TranslationMode || (state.selectedCategory != null && state.selectedCategory.isEligible))

            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                TextPrimary(
                    text = stringResource(com.rafalskrzypczyk.core.R.string.btn_cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
            TextButton(
                onClick = rememberDebouncedClick {
                    onDismiss()
                    onStartSession(
                        state.selectedMode,
                        state.selectedCategory?.id,
                        state.selectedCriterion,
                        state.selectedLimit
                    )
                },
                enabled = isStartEnabled
            ) {
                TextPrimary(
                    text = stringResource(R.string.revisions_start_btn),
                    color = if (isStartEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    )
}

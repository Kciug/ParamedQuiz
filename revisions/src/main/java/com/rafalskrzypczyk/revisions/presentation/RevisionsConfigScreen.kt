package com.rafalskrzypczyk.revisions.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigState
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigUIEvents

@Composable
fun RevisionsConfigScreen(
    state: RevisionsConfigState,
    onEvent: (RevisionsConfigUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onStartSession: (QuizMode, Long?, RevisionCriterion, Int?) -> Unit
) {
    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NavTopBar(
                title = stringResource(R.string.revisions_config_title),
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            when (state.responseState) {
                ResponseState.Idle -> {}
                ResponseState.Loading -> Loading()
                is ResponseState.Error -> ErrorDialog(state.responseState.message) {
                    onNavigateBack()
                }
                ResponseState.Success -> {
                    RevisionsConfigScreenContent(
                        state = state,
                        onEvent = onEvent,
                        onTriggerCategoryDialog = { showCategoryDialog = true },
                        onStartSession = onStartSession
                    )
                }
            }
        }
    }

    if (showCategoryDialog && state.categoriesList.isNotEmpty()) {
        CategorySelectionDialog(
            categories = state.categoriesList,
            selectedCategory = state.selectedCategory,
            onCategorySelected = { onEvent(RevisionsConfigUIEvents.SelectCategory(it)) },
            onDismiss = { showCategoryDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RevisionsConfigScreenContent(
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

@Composable
private fun ModeSelectorCard(
    mode: QuizMode,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.DEFAULT_PADDING)
        ) {
            val icon = ModeInfoProvider.getIcon(mode)
            val color = ModeInfoProvider.getColor(mode)

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (enabled) color else color.copy(alpha = 0.38f),
                        shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                TextHeadline(
                    text = when (mode) {
                        QuizMode.MainMode -> "Tryb Główny"
                        QuizMode.CemMode -> "Tryb CEM"
                        QuizMode.TranslationMode -> "Tryb Tłumaczeń"
                        else -> ""
                    },
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    textAlign = TextAlign.Start
                )
                
                TextPrimary(
                    text = if (enabled) {
                        when (mode) {
                            QuizMode.MainMode -> "Utrwalaj pytania ze standardowych kategorii"
                            QuizMode.CemMode -> "Powtarzaj pytania z oficjalnej bazy CEM"
                            QuizMode.TranslationMode -> "Przećwicz słownictwo z trybu tłumaczeń"
                            else -> ""
                        }
                    } else {
                        stringResource(R.string.revisions_mode_not_enough_answers)
                    },
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun CategorySelectionTriggerCard(
    category: RevisionCategory?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TextCaption(text = "Wybrana kategoria")
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                TextHeadline(
                    text = category?.title ?: "Wybierz kategorię..."
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CategorySelectionLoadingCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
            TextPrimary(text = "Pobieranie kategorii...")
        }
    }
}

@Composable
private fun RevisionsChoiceChip(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT),
        color = containerColor,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextPrimary(
                text = title,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.38f),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun CategorySelectionDialog(
    categories: List<RevisionCategory>,
    selectedCategory: RevisionCategory?,
    onCategorySelected: (RevisionCategory) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = Dimens.LARGE_PADDING)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.DEFAULT_PADDING)
            ) {
                TextHeadline(
                    text = stringResource(R.string.revisions_select_pool)
                )
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(categories) { category ->
                        val isSelected = category.id == selectedCategory?.id
                        val containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }

                        Card(
                            onClick = {
                                if (category.isEligible) {
                                    onCategorySelected(category)
                                    onDismiss()
                                }
                            },
                            enabled = category.isEligible,
                            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(Dimens.DEFAULT_PADDING)
                                    .alpha(if (category.isEligible) 1.0f else 0.5f)
                            ) {
                                TextPrimary(
                                    text = category.title,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                                if (category.isEligible) {
                                    TextCaption(text = "Dostępne pytania: ${category.totalQuestionsCount}")
                                } else {
                                    TextCaption(
                                        text = stringResource(
                                            R.string.revisions_category_not_enough_answers,
                                            category.answeredQuestionsCount
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                ButtonSecondary(
                    title = stringResource(android.R.string.cancel),
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
            Column {
                TextHeadline(
                    text = stringResource(R.string.revisions_empty_state_title)
                )
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                TextPrimary(
                    text = stringResource(R.string.revisions_empty_state_msg),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RevisionsConfigScreenPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RevisionsConfigScreen(
                state = RevisionsConfigState(
                    selectedMode = QuizMode.MainMode,
                    categoriesList = listOf(
                        RevisionCategory(
                            id = 1L,
                            title = "Kardiologia",
                            mode = QuizMode.MainMode,
                            totalQuestionsCount = 50,
                            answeredQuestionsCount = 12,
                            isEligible = true
                        ),
                        RevisionCategory(
                            id = 2L,
                            title = "Pediatria",
                            mode = QuizMode.MainMode,
                            totalQuestionsCount = 30,
                            answeredQuestionsCount = 2,
                            isEligible = false
                        )
                    ),
                    selectedCategory = RevisionCategory(
                        id = 1L,
                        title = "Kardiologia",
                        mode = QuizMode.MainMode,
                        totalQuestionsCount = 50,
                        answeredQuestionsCount = 12,
                        isEligible = true
                    ),
                    selectedCriterion = RevisionCriterion.WORST,
                    selectedLimit = 10,
                    availableQuestionsCount = 12,
                    availableLimits = listOf(10, null),
                    responseState = ResponseState.Success,
                    isModeEligible = true
                ),
                onEvent = {},
                onNavigateBack = {},
                onStartSession = { _, _, _, _ -> }
            )
        }
    }
}

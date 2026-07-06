package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.R

@Composable
fun RevisionsConfigScreenContent(
    onSelectMode: (QuizMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextHeadline(
            text = stringResource(R.string.revisions_select_mode)
        )

        listOf(QuizMode.MainMode, QuizMode.CemMode, QuizMode.TranslationMode).forEach { mode ->
            ModeSelectorCard(
                mode = mode,
                enabled = true,
                onClick = { onSelectMode(mode) }
            )
        }
    }
}

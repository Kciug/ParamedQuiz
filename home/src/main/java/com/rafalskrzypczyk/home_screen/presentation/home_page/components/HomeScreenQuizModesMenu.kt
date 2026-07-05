package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.home.R

@Composable
fun HomeScreenQuizModesMenu(
    modifier: Modifier = Modifier,
    isTranslationModeUnlocked: Boolean,
    isSwipeModeUnlocked: Boolean,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: (Boolean) -> Unit,
    onNavigateToTranslationMode: () -> Unit,
    onNavigateToCemMode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextHeadline(stringResource(R.string.title_quiz_modes))
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
            description = stringResource(R.string.mode_quiz_desc),
            mode = QuizMode.MainMode
        ) { onNavigateToMainMode() }
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
            description = stringResource(R.string.mode_swipe_desc),
            mode = QuizMode.SwipeMode
        ) { onNavigateToSwipeMode(false) }
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
            description = stringResource(R.string.mode_translation_desc),
            mode = QuizMode.TranslationMode
        ) { onNavigateToTranslationMode() }
        QuizModeButton(
            title = stringResource(R.string.title_cem_mode),
            description = stringResource(R.string.mode_cem_desc),
            mode = QuizMode.CemMode
        ) { onNavigateToCemMode() }
    }
}

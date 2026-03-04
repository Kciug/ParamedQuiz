package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    onNavigateToTranslationMode: () -> Unit
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
            mode = QuizMode.TranslationMode,
            locked = !isTranslationModeUnlocked
        ) { onNavigateToTranslationMode() }
        Card (
            modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING_SMALL),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                Color.Transparent
                            ),
                            endY = 100f
                        ),
                    )
                    .padding(
                        horizontal = Dimens.DEFAULT_PADDING,
                        vertical = Dimens.DEFAULT_PADDING * 2
                    ),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextHeadline(
                    text = stringResource(R.string.more_modes_soon),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

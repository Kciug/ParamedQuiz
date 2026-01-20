package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.InfoDialog
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.top_bars.MainTopBar
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.score.domain.StreakState

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onEvent: (HomeUIEvents) -> Unit,
    onNavigateToUserPanel: () -> Unit,
    onNavigateToDailyExercise: () -> Unit,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: () -> Unit,
    onNavigateToTranslationMode: () -> Unit,
    onNavigateToDevOptions: () -> Unit
) {
    var showDailyExerciseAlreadyDoneAlert by remember { mutableStateOf(false) }

    var showRevisionsUnavailableAlert by remember { mutableStateOf(false) }

    val addons = listOf(
        Addon(
            title = stringResource(R.string.title_addon_daily_exercises),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_dailyexercise,
            highlighted = state.isNewDailyExerciseAvailable,
            isAvailable = state.isNewDailyExerciseAvailable
        ) {
            if (state.isNewDailyExerciseAvailable) {
                onNavigateToDailyExercise()
            } else {
                showDailyExerciseAlreadyDoneAlert = true
            }
        },
        Addon(
            title = stringResource(R.string.title_addon_review),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_revisions,
            isAvailable = false,
        ) { showRevisionsUnavailableAlert = true },
    )

    LaunchedEffect(Unit) {
        onEvent.invoke(HomeUIEvents.GetData)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MainTopBar(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                userAvatar = state.userAvatar,
                userStreakPending = state.userStreakState == StreakState.PENDING,
                onClick = onNavigateToDevOptions
            ) { onNavigateToUserPanel() }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
            )

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeCard(
                userName = state.userName,
                modifier = Modifier
                    .padding(horizontal = Dimens.DEFAULT_PADDING)
                    .padding(top = Dimens.DEFAULT_PADDING)
            )
            HomeScreenAddonsMenu(addons = addons)
            HomeScreenQuizModesMenu(
                onNavigateToMainMode = onNavigateToMainMode,
                onNavigateToSwipeMode = onNavigateToSwipeMode,
                onNavigateToTranslationMode = onNavigateToTranslationMode
            )

            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)))
        }
    }

    if(showDailyExerciseAlreadyDoneAlert) {
        InfoDialog(
            title = stringResource(id = R.string.title_daily_exercise_already_done),
            message = stringResource(id = R.string.text_daily_exercise_already_done),
            icon = Icons.Default.Check,
            headerColor = MQGreen,
            onDismiss = { showDailyExerciseAlreadyDoneAlert = false }
        )
    }

    if(showRevisionsUnavailableAlert) {
        InfoDialog(
            title = stringResource(id = R.string.title_revisions_unavailable),
            message = stringResource(id = R.string.text_revisions_unavailable),
            icon = Icons.Default.Upcoming,
            headerColor = MQYellow,
            headerContentColor = Color.Black,
            onDismiss = { showRevisionsUnavailableAlert = false }
        )
    }
}

@Composable
fun HomeScreenAddonsMenu(
    modifier: Modifier = Modifier,
    addons: List<Addon>
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextHeadline(
            text = stringResource(R.string.title_addons),
            modifier = Modifier.padding(start = Dimens.DEFAULT_PADDING, top = Dimens.DEFAULT_PADDING)
        )
        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.DEFAULT_PADDING),
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            items(addons, key = { it.title }) { addon ->
                AddonButton(
                    //modifier = cardWidthModifier,
                    addon = addon
                )
            }
        }
    }
}

@Composable
fun HomeScreenQuizModesMenu(
    modifier: Modifier = Modifier,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: () -> Unit,
    onNavigateToTranslationMode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
    ) {
        TextHeadline(stringResource(R.string.title_quiz_modes))
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
            description = stringResource(R.string.mode_quiz_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_mainmode
        ) { onNavigateToMainMode() }
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
            description = stringResource(R.string.mode_swipe_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_swipemode
        ) { onNavigateToSwipeMode() }
        QuizModeButton(
            title = "Translations", // Hardcoded string as requested/implied no new strings
            description = "Learn vocabulary",
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_dailyexercise // Placeholder icon
        ) { onNavigateToTranslationMode() }
        Card (
            modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING),
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
                    text = "Więcej trybów już wkrótce!",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(heightDp = 360, widthDp = 800)
@Composable
private fun HomeScreenPreview() {
    ParamedQuizTheme {
        Surface {
            HomeScreen(
                state = HomeScreenState(
                    isUserLoggedIn = true,
                    userScore = 95600,
                    userStreak = 24,
                    isNewDailyExerciseAvailable = true
                ),
                onEvent = {},
                onNavigateToUserPanel = {},
                onNavigateToMainMode = {},
                onNavigateToSwipeMode = {},
                onNavigateToTranslationMode = {},
                onNavigateToDailyExercise = {},
                onNavigateToDevOptions = {}
            )
        }
    }
}
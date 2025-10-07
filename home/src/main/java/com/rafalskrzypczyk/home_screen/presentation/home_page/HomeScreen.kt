package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.MainTopBar
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.score.domain.StreakState

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onNavigateToUserPanel: () -> Unit,
    onNavigateToDailyExercise: () -> Unit,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: () -> Unit
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
//        Addon(
//            title = stringResource(R.string.title_addon_first_aid),
//            imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
//            color = Color.Blue
//        ) {}
    )

    Scaffold(
        topBar = {
            MainTopBar(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                userAvatar = state.userAvatar,
                userStreakPending = state.userStreakState == StreakState.PENDING,
            ) { onNavigateToUserPanel() }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreenAddonsMenu(addons = addons)
            HomeScreenQuizModesMenu(
                onNavigateToMainMode = onNavigateToMainMode,
                onNavigateToSwipeMode = onNavigateToSwipeMode
            )
        }
    }

    if(showDailyExerciseAlreadyDoneAlert) {
        DailyExerciseDoneDialog {
            showDailyExerciseAlreadyDoneAlert = false
        }
    }

    if(showRevisionsUnavailableAlert) {
        RevisionsUnavailableDialog {
            showRevisionsUnavailableAlert = false
        }
    }
}

@Composable
fun HomeScreenAddonsMenu(
    modifier: Modifier = Modifier,
    addons: List<Addon>
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidthModifier = if(isLandscape) Modifier else Modifier.width(screenWidth * 0.4f)

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
    onNavigateToSwipeMode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
    ) {
        TextHeadline(stringResource(R.string.title_quiz_modes))
        QuizModeButton(
            title = stringResource(R.string.title_mode_quiz),
            description = stringResource(R.string.mode_quiz_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_mainmode
        ) { onNavigateToMainMode() }
        QuizModeButton(
            title = stringResource(R.string.title_mode_swipe),
            description = stringResource(R.string.mode_swipe_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_swipemode
        ) { onNavigateToSwipeMode() }
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
                onNavigateToUserPanel = {},
                onNavigateToMainMode = {},
                onNavigateToSwipeMode = {},
                onNavigateToDailyExercise = {}
            )
        }
    }
}
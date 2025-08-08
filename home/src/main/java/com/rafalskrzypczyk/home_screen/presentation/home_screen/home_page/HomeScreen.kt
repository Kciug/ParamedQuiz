package com.rafalskrzypczyk.home_screen.presentation.home_screen.home_page

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onNavigateToUserPanel: () -> Unit,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeScreenUserPanel(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                userAvatar = null
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreenUserPanel(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                userAvatar = null
            )
            Column(modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                HomeScreenAddonsMenu(
                    onNavigateToDailyExercise = {},
                    onNavigateToReview = {}
                )
                HomeScreenQuizModesMenu(
                    onNavigateToMainMode = onNavigateToMainMode,
                    onNavigateToSwipeMode = onNavigateToSwipeMode
                )
            }
        }
    }
}

@Composable
fun HomeScreenAddonsMenu(
    modifier: Modifier = Modifier,
    onNavigateToDailyExercise: () -> Unit,
    onNavigateToReview: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextHeadline(
            text = "Praktyka na dziś?",
            modifier = Modifier.padding(start = Dimens.DEFAULT_PADDING, top = Dimens.DEFAULT_PADDING)
        )
        Spacer(modifier = Modifier.height(Dimens.SMALL_PADDING))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING)
        ) {
            val cardWidthModifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 0.4f)

            Spacer(modifier = Modifier.width(Dimens.DEFAULT_PADDING - Dimens.SMALL_PADDING))

            AdditionalModeButton(
                modifier = cardWidthModifier,
                title = "Zadanie dnia",
                imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                highlighted = true,
            ) { onNavigateToDailyExercise() }
            AdditionalModeButton(
                modifier = cardWidthModifier,
                title = "Powtórki",
                imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                highlighted = false,
            ) { onNavigateToReview() }

            Spacer(modifier = Modifier.width(Dimens.DEFAULT_PADDING - Dimens.SMALL_PADDING))
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
        verticalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING)
    ) {
        TextHeadline("Czas na quiz!")
        QuizModeButton(
            title = stringResource(R.string.title_quiz_mode),
            description = "Zdobywaj wiedzę grając w quiz!",
            imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256
        ) { onNavigateToMainMode() }
        QuizModeButton(
            title = stringResource(R.string.title_swipe_mode),
            description = "Zdobywaj wiedzę grając w quiz!",
            imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256
        ) { onNavigateToSwipeMode() }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    ParamedQuizTheme {
        Surface {
            HomeScreen(
                state = HomeScreenState(
                    isUserLoggedIn = true,
                    userScore = 95600,
                    userStreak = 24
                ),
                onNavigateToUserPanel = {},
                onNavigateToMainMode = {},
                onNavigateToSwipeMode = {},
            )
        }
    }
}
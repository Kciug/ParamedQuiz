package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.NavTopBar
import com.rafalskrzypczyk.core.composables.SettingsButton
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.composables.UserStreakLabel
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme


@Composable
fun UserPageScreen(
    state: UserPageState,
    onEvent: (UserPageUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onUserSettings: () -> Unit
) {
    LaunchedEffect(Unit) {
        onEvent.invoke(UserPageUIEvents.RefreshUserData)
    }

    Scaffold(
        topBar = {
            NavTopBar(
                actions = { SettingsButton { onUserSettings() } }
            ) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserPageUserDetails(
                userName = state.userName,
                userPoints = state.userScore,
                userStreak = state.userStreak
            )

            Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))

            UserStatisticsComponent(
                overallResult = state.overallResult,
                mainModeResult = state.mainModeResult,
                swipeModeResult = state.swipeModeResult
            )
        }
    }
}

@Composable
fun UserPageUserDetails(
    modifier: Modifier = Modifier,
    userName: String,
    userPoints: Int,
    userStreak: Int
) {
    Box(
        modifier = modifier
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.IMAGE_SIZE / 2)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background
                        )
                    ),
                    shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)
                )
                .padding(top = Dimens.IMAGE_SIZE / 2 + Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            TextHeadline(userName)
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserPointsLabel(value = userPoints)
                UserStreakLabel(value = userStreak)
            }
        }

        Image(
            painter = painterResource(R.drawable.avatar_default),
            contentDescription = stringResource(R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.Transparent)
                .size(Dimens.IMAGE_SIZE)
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserPagePreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            UserPageScreen(
                state = UserPageState(
                    userName = stringResource(R.string.placeholder_short),
                    userScore = 2137,
                    userStreak = 15
                ),
                onNavigateBack = {},
                onUserSettings = {},
                onEvent = {}
            )
        }
    }
}
package com.rafalskrzypczyk.home_screen.presentation.user_page

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.SettingsButton
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.composables.UserStreakLabel
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.UserStatisticsComponent
import com.rafalskrzypczyk.score.domain.StreakState


@Composable
fun UserPageScreen(
    state: UserPageState,
    onEvent: (UserPageUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenRegistration: () -> Unit
) {
    var topBarHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        onEvent.invoke(UserPageUIEvents.RefreshUserData)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))

        Box(contentAlignment = Alignment.TopCenter){
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(with(LocalDensity.current) {
                    topBarHeight.toDp() + WindowInsets.safeDrawing.getTop(this).toDp()
                }))
                
                if (state.isUserLoggedIn) {
                    UserPageUserDetails(
                        userName = state.userName,
                        userPoints = state.userScore,
                        userStreak = state.userStreak,
                        userStreakState = state.userStreakState,
                        isPremium = state.isPremium
                    )
                } else {
                    GuestUserSection(
                        userPoints = state.userScore,
                        userStreak = state.userStreak,
                        userStreakState = state.userStreakState,
                        onRegisterClick = onOpenRegistration
                    )
                }

                Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))

                UserStatisticsComponent(
                    overallResultAvailable = state.overallResultAvailable,
                    mainModeResultAvailable = state.mainModeResultAvailable,
                    swipeModeResultAvailable = state.swipeModeResultAvailable,
                    translationModeResultAvailable = state.translationModeResultAvailable,
                    overallResult = state.overallResult,
                    mainModeResult = state.mainModeResult,
                    swipeModeResult = state.swipeModeResult,
                    translationModeResult = state.translationModeResult,
                    bestWorstQuestions = state.bestWorstQuestions,
                    onNextMode = { onEvent(UserPageUIEvents.OnNextMode) },
                    onPreviousMode = { onEvent(UserPageUIEvents.OnPreviousMode) },
                )

                Spacer(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)))
            }

            NavTopBar(
                modifier = Modifier.onGloballyPositioned {
                    topBarHeight = it.size.height
                },
                actions = { 
                    SettingsButton { onOpenSettings() } 
                }
            ) { onNavigateBack() }
        }
    }
}

@Composable
fun UserPageUserDetails(
    modifier: Modifier = Modifier,
    userName: String,
    userPoints: Int,
    userStreak: Int,
    userStreakState: StreakState,
    isPremium: Boolean = false
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
                UserStreakLabel(
                    value = userStreak,
                    isPending = userStreakState == StreakState.PENDING
                )
            }
        }

        val borderModifier = if (isPremium) {
            Modifier.border(Dimens.OUTLINE_THICKNESS, MQYellow, CircleShape)
        } else {
            Modifier
        }

        Box(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(R.drawable.avatar_default),
                contentDescription = stringResource(R.string.desc_user_avatar),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .then(borderModifier)
                    .size(Dimens.IMAGE_SIZE)
            )

            if (isPremium) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = Dimens.SMALL_PADDING)
                        .background(MQYellow, RoundedCornerShape(Dimens.RADIUS_SMALL))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(Dimens.RADIUS_SMALL)
                        )
                        .padding(horizontal = Dimens.SMALL_PADDING, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.premium_badge),
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GuestUserSection(
    modifier: Modifier = Modifier,
    userPoints: Int,
    userStreak: Int,
    userStreakState: StreakState,
    onRegisterClick: () -> Unit
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
                .padding(top = Dimens.IMAGE_SIZE / 2 + Dimens.DEFAULT_PADDING)
                .padding(horizontal = Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            TextHeadline(stringResource(com.rafalskrzypczyk.home.R.string.title_guest_user))
            TextPrimary(
                text = stringResource(com.rafalskrzypczyk.home.R.string.guest_user_msg),
                textAlign = TextAlign.Center
            )
            ButtonPrimary(
                title = stringResource(com.rafalskrzypczyk.home.R.string.btn_register_login),
                onClick = onRegisterClick
            )
            HorizontalDivider()
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserPointsLabel(value = userPoints)
                UserStreakLabel(
                    value = userStreak,
                    isPending = userStreakState == StreakState.PENDING
                )
            }
        }

        Image(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .size(Dimens.IMAGE_SIZE)
                .align(Alignment.TopCenter)
                .padding(Dimens.DEFAULT_PADDING),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
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
                    isUserLoggedIn = true,
                    userName = stringResource(R.string.placeholder_short),
                    userScore = 2137,
                    userStreak = 15
                ),
                onNavigateBack = {},
                onOpenSettings = {},
                onOpenRegistration = {},
                onEvent = {}
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserPageGuestPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            UserPageScreen(
                state = UserPageState(
                    isUserLoggedIn = false
                ),
                onNavigateBack = {},
                onOpenSettings = {},
                onOpenRegistration = {},
                onEvent = {}
            )
        }
    }
}

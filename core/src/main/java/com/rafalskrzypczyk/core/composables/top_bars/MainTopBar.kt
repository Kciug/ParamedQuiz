package com.rafalskrzypczyk.core.composables.top_bars

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.BackButton
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.UserAvatarAction
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.composables.UserStreakLabel
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    userScore: Int,
    userStreak: Int,
    userStreakPending: Boolean,
    isUserLoggedIn: Boolean,
    userAvatar: String?,
    onClick: () -> Unit = {},
    onNavigateToUserPanel: () -> Unit,
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
            .clip(RoundedCornerShape(bottomStart = Dimens.RADIUS_DEFAULT, bottomEnd = Dimens.RADIUS_DEFAULT))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                ),
            )
            .combinedClickable(
                onLongClick = onClick,
                onClick = {}
            )
            .padding(Dimens.DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UserPointsLabel(
            modifier = Modifier.weight(1f),
            value = userScore
        )
        UserStreakLabel(
            value = userStreak,
            isPending = userStreakPending
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            UserAvatarAction(
                isUserLoggedIn = isUserLoggedIn,
                userAvatar = userAvatar
            ) { onNavigateToUserPanel() }
        }
    }
}

@Composable
fun MainTopBarWithNav(
    modifier: Modifier = Modifier,
    userScore: Int,
    userStreak: Int,
    userStreakPending: Boolean,
    isUserLoggedIn: Boolean,
    userAvatar: String?,
    onNavigateBack: () -> Unit,
    onNavigateToUserPanel: () -> Unit,
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
            .clip(RoundedCornerShape(bottomStart = Dimens.RADIUS_DEFAULT, bottomEnd = Dimens.RADIUS_DEFAULT))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                ),
            )
            .padding(Dimens.DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(showBackground = false) { onNavigateBack() }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UserPointsLabel(
                value = userScore
            )
            UserStreakLabel(
                value = userStreak,
                isPending = userStreakPending
            )
        }
        UserAvatarAction(
            isUserLoggedIn = isUserLoggedIn,
            userAvatar = userAvatar
        ) { onNavigateToUserPanel() }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MainTopBarPreview() {
    ParamedQuizTheme {
        Surface {
            MainTopBar(
                userScore = 100,
                userStreak = 10,
                isUserLoggedIn = true,
                userAvatar = null,
                userStreakPending = false
            ) {}
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MainTopBarLoggedOutPreview() {
    ParamedQuizTheme {
        Surface {
            MainTopBar(
                userScore = 2137995,
                userStreak = 15,
                isUserLoggedIn = false,
                userAvatar = null,
                userStreakPending = true
            ) {}
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MainTopBarNavigationPreview() {
    ParamedQuizTheme {
        Surface {
            MainTopBarWithNav (
                userScore = 2137995,
                userStreak = 15,
                isUserLoggedIn = true,
                userAvatar = null,
                userStreakPending = false,
                onNavigateBack = { }
            ) {}
        }
    }
}
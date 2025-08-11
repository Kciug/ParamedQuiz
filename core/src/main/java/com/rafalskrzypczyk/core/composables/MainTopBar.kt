package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    userScore: Int,
    userStreak: Int,
    isUserLoggedIn: Boolean,
    userAvatar: String?,
    onNavigateToUserPanel: () -> Unit
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UserPointsLabel(
            modifier = Modifier.weight(1f),
            value = userScore
        )
        UserStreakLabel(value = userStreak)
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
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MainTopBarPreview() {
    ParamedQuizTheme {
        Surface {
            MainTopBar(
                userScore = 100,
                userStreak = 10,
                isUserLoggedIn = true,
                userAvatar = null
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
                userAvatar = null
            ) {}
        }
    }
}
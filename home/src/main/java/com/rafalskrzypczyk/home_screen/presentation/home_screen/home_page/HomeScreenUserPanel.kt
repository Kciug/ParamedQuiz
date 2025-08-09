package com.rafalskrzypczyk.home_screen.presentation.home_screen.home_page

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.composables.UserStreakLabel
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home_screen.presentation.home_screen.UserAvatar

@Composable
fun HomeScreenUserPanel(
    modifier: Modifier = Modifier,
    userScore: Int,
    userStreak: Int,
    isUserLoggedIn: Boolean,
    userAvatar: String?,
    onNavigateToUserPanel: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = Dimens.RADIUS_DEFAULT, bottomEnd = Dimens.RADIUS_DEFAULT))
            .background(MaterialTheme.colorScheme.surface)
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
            modifier = Modifier.weight(1f).clickable(onClick = onNavigateToUserPanel),
            contentAlignment = Alignment.CenterEnd
        ) {
            if(isUserLoggedIn){
                UserAvatar()
            } else {
                Icon(
                    modifier = Modifier.size(Dimens.IMAGE_SIZE_SMALL),
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar)
                )
            }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun HomeScreenUserPanelPreview() {
    ParamedQuizTheme {
        Surface {
            HomeScreenUserPanel(
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
private fun HomeScreenUserPanelPreviewLoggedOut() {
    ParamedQuizTheme {
        Surface {
            HomeScreenUserPanel(
                userScore = 2137995,
                userStreak = 15,
                isUserLoggedIn = false,
                userAvatar = null
            ) {}
        }
    }
}
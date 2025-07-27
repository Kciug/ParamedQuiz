package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.NavigationTopBar


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
            NavigationTopBar(
                title = stringResource(com.rafalskrzypczyk.home.R.string.title_user_page),
                onNavigateBack = onNavigateBack
            ) {
                IconButton(onClick = { onUserSettings() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.desc_settings)
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            UserPageUserDetails(state.userName, state.score)
        }
    }
}

@Composable
fun UserPageUserDetails(
    userName: String,
    score: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        Image(
            painter = painterResource(R.drawable.avatar_default),
            contentDescription = stringResource(R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.Transparent)
                .size(Dimens.IMAGE_SIZE)
        )

        HorizontalDivider(Modifier.padding(Dimens.SMALL_PADDING))

        Text(
            text = userName,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = Dimens.SMALL_PADDING)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("STRIKE")
            Spacer(Modifier.weight(1f))
            Row {
                Icon(
                    imageVector = Icons.Default.CurrencyBitcoin,
                    contentDescription = stringResource(com.rafalskrzypczyk.home.R.string.desc_points)
                )
                TextPrimary(score)
            }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserPagePreview() {
    Surface {
        UserPageScreen(
            state = UserPageState(
                userName = stringResource(R.string.placeholder_short),
                score = "1234"
            ),
            onNavigateBack = {},
            onUserSettings = {},
            onEvent = {}
        )
    }
}
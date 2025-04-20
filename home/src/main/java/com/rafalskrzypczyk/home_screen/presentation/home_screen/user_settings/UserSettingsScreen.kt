package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.ui.NavigationTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R

@Composable
fun UserSettingsScreen(
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val settingsList = listOf(
        UserSettingsElement(stringResource(R.string.title_change_username)) {
            UserSettingsChangeUserName(state.usernameValidationMessage) { onEvent(UserSettingsUIEvents.ChangeUsername(it)) }
        },
        UserSettingsElement(stringResource(R.string.title_change_password)) {
            UserSettingsChangePassword(state.passwordValidationMessage) { oldPassword, newPassword, newPasswordRepeat ->
                onEvent(UserSettingsUIEvents.ChangePassword(oldPassword, newPassword, newPasswordRepeat))
            }
        },
        UserSettingsElement(stringResource(R.string.title_delete_account)) {
            UserSettingsDeleteAccount { onEvent(UserSettingsUIEvents.DeleteAccount(it)) }
        }
    )

    Scaffold (
        topBar = {
            NavigationTopBar(
                title = stringResource(R.string.title_settings),
                onNavigateBack = onNavigateBack,
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when(state.responseState) {
            is ResponseState.Error -> ErrorDialog(state.responseState.message) { }
            ResponseState.Idle -> UserSettingsContent(
                modifier = modifier,
                state = state,
                onEvent = onEvent,
                onSignOut = onSignOut,
                settingsList = settingsList
            )
            ResponseState.Loading -> Loading(modifier = modifier)
            ResponseState.Success -> UserSettingsSuccessView(
                modifier = modifier,
                onConfirm = when(state.successConfirmAction) {
                    UserSettingsConfirmAction.NAVIGATE_OUT -> { { onSignOut() } }
                    UserSettingsConfirmAction.CLEAR_STATE -> { { onEvent(UserSettingsUIEvents.ClearState) } }
                }
            )
        }
    }
}

@Composable
private fun UserSettingsContent(
    modifier: Modifier,
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onSignOut: () -> Unit,
    settingsList: List<UserSettingsElement>
) {
    Column (modifier = modifier) {
        UserSettingsUserDetails(userName = state.userName, userEmail = state.userEmail)

        HorizontalDivider(modifier = Modifier.padding(top = Dimens.SMALL_PADDING))

        UserSettingsList(
            modifier = Modifier.weight(1f),
            settingsList = settingsList
        )

        ButtonSecondary(
            title = stringResource(R.string.btn_logout),
            onClick = {
                onEvent.invoke(UserSettingsUIEvents.SignOut)
                onSignOut()
            },
            modifier = Modifier.padding(Dimens.ELEMENTS_SPACING)
        )
    }
}

@Composable
private fun UserSettingsUserDetails(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String
) {
    Row (
        modifier = modifier
            .padding(Dimens.SMALL_PADDING)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.avatar_default),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(Dimens.IMAGE_SIZE_SMALL)
                .clip(CircleShape)
                .aspectRatio(1f)
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))

        Column {
            Text(text = userName, style = MaterialTheme.typography.bodyLarge)
            Text(text = userEmail, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun UserSettingsList(
    modifier: Modifier = Modifier,
    settingsList: List<UserSettingsElement>
) {
    var expandedElementId by remember { mutableStateOf<Int?>(null) }

    LazyColumn (
        modifier = modifier.fillMaxSize()
    ) {
        settingsList.forEachIndexed { index, element ->
            item {
                UserSettingsItem(
                    title = element.title,
                    onClick = { expandedElementId = if(expandedElementId == index) null else index },
                    isExpanded = expandedElementId == index,
                    content = element.content
                )
            }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserSettingsPreview() {
    ParamedQuizTheme {
        Surface {
            UserSettingsScreen(
                state = UserSettingsState(
                    userName = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_name),
                    userEmail = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_email)
                ),
                onEvent = {},
                onNavigateBack = {},
                onSignOut = {}
            )
        }
    }
}


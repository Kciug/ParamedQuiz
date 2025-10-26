package com.rafalskrzypczyk.home_screen.presentation.user_settings

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.home.R

@Composable
fun UserSettingsScreen(
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    val context = LocalContext.current

    val settingsList = if(state.accountType == UserAuthenticationMethod.PASSWORD) {
        listOf(
            UserSettingsElement(stringResource(R.string.title_change_username)) {
                UserSettingsChangeUserName(state.usernameValidationMessage) { onEvent(UserSettingsUIEvents.ChangeUsername(it)) }
            },
            UserSettingsElement(stringResource(R.string.title_change_password)) {
                UserSettingsChangePassword(state.passwordValidationMessage) { oldPassword, newPassword, newPasswordRepeat ->
                    onEvent(UserSettingsUIEvents.ChangePassword(oldPassword, newPassword, newPasswordRepeat))
                }
            },
            UserSettingsElement(stringResource(R.string.title_delete_account)) {
                UserSettingsDeleteAccountWithPassword { onEvent(UserSettingsUIEvents.DeleteAccount(it)) }
            }
        )
    } else {
        listOf(
            UserSettingsElement(stringResource(R.string.title_change_username)) {
                UserSettingsChangeUserName(state.usernameValidationMessage) { onEvent(UserSettingsUIEvents.ChangeUsername(it)) }
            },
            UserSettingsElement(stringResource(R.string.title_delete_account)) {
                UserSettingsDeleteAccountForProvider { onEvent(UserSettingsUIEvents.DeleteAccountForProvider(context)) }
            }
        )
    }

    Scaffold (
        topBar = {
            NavTopBar(title = stringResource(R.string.title_settings)) { onNavigateBack() }
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
        BrandingElement()
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
            .padding(horizontal = Dimens.DEFAULT_PADDING)
            .padding(vertical = Dimens.SMALL_PADDING)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.avatar_default),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = Dimens.ELEMENTS_SPACING)
                .height(Dimens.IMAGE_SIZE_SMALL)
                .clip(CircleShape)
                .aspectRatio(1f)
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))

        Column {
            TextPrimary(text = userName)
            TextCaption(text = userEmail)
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
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.DEFAULT_PADDING)
    ) {
        settingsList.forEachIndexed { index, element ->
            item {
                UserSettingsItem(
                    modifier = Modifier.padding(vertical = Dimens.ELEMENTS_SPACING_SMALL),
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
                onSignOut = {},
            )
        }
    }
}


package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.SettingsCategoryHeader
import com.rafalskrzypczyk.core.composables.SettingsDialog
import com.rafalskrzypczyk.core.composables.SettingsItemRow
import com.rafalskrzypczyk.core.composables.SettingsSwitchRow
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
    val successMsg = stringResource(com.rafalskrzypczyk.core.R.string.desc_success)

    LaunchedEffect(state.showSuccessToast) {
        if (state.showSuccessToast) {
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            onEvent(UserSettingsUIEvents.OnSuccessToastShown)
        }
    }

    Scaffold (
        topBar = {
            NavTopBar(title = stringResource(R.string.title_settings)) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when(state.responseState) {
            is ResponseState.Error -> ErrorDialog(state.responseState.message) { onEvent(UserSettingsUIEvents.ClearState) }
            ResponseState.Idle, ResponseState.Loading -> { // Show content even if loading overlay is on top
                Box(modifier = modifier.fillMaxSize()) {
                    UserSettingsContent(
                        state = state,
                        onEvent = onEvent,
                        onSignOut = onSignOut
                    )
                    
                    if (state.responseState == ResponseState.Loading) {
                        Loading()
                    }
                }
            }
            ResponseState.Success -> UserSettingsSuccessView(
                modifier = modifier,
                onConfirm = when(state.successConfirmAction) {
                    UserSettingsConfirmAction.NAVIGATE_OUT -> { { onSignOut() } }
                    UserSettingsConfirmAction.CLEAR_STATE -> { { onEvent(UserSettingsUIEvents.ClearState) } }
                }
            )
        }
    }

    if (state.showChangeUsernameDialog) {
        SettingsDialog(
            title = stringResource(R.string.title_change_username),
            onDismiss = { onEvent(UserSettingsUIEvents.ToggleChangeUsernameDialog(false)) },
            content = {
                 UserSettingsChangeUserName(state.usernameValidationMessage) { onEvent(UserSettingsUIEvents.ChangeUsername(it)) }
            }
        )
    }

    if (state.showChangePasswordDialog) {
        SettingsDialog(
            title = stringResource(R.string.title_change_password),
            onDismiss = { onEvent(UserSettingsUIEvents.ToggleChangePasswordDialog(false)) },
            content = {
                UserSettingsChangePassword(state.passwordValidationMessage) { old, new, repeat ->
                    onEvent(UserSettingsUIEvents.ChangePassword(old, new, repeat))
                }
            }
        )
    }

    if (state.showDeleteAccountDialog) {
        SettingsDialog(
            title = stringResource(R.string.title_delete_account),
            onDismiss = { onEvent(UserSettingsUIEvents.ToggleDeleteAccountDialog(false)) },
            content = {
                if (state.accountType == UserAuthenticationMethod.PASSWORD) {
                    UserSettingsDeleteAccountWithPassword { onEvent(UserSettingsUIEvents.DeleteAccount(it)) }
                } else {
                    UserSettingsDeleteAccountForProvider { onEvent(UserSettingsUIEvents.DeleteAccountForProvider(context)) }
                }
            }
        )
    }
}

@Composable
private fun UserSettingsContent(
    modifier: Modifier = Modifier,
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onSignOut: () -> Unit
) {
    Column (modifier = modifier.fillMaxSize()) {
        UserSettingsUserDetails(
            userName = if(state.isAnonymous) stringResource(R.string.user_anonymous) else state.userName,
            userEmail = state.userEmail
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            if (!state.isAnonymous) {
                item {
                    SettingsCategoryHeader("Konto")
                }
                
                item {
                    SettingsItemRow(
                        title = stringResource(R.string.title_change_username),
                        icon = Icons.Outlined.Badge,
                        onClick = { onEvent(UserSettingsUIEvents.ToggleChangeUsernameDialog(true)) }
                    )
                }
                
                if (state.accountType == UserAuthenticationMethod.PASSWORD) {
                    item {
                        SettingsItemRow(
                            title = stringResource(R.string.title_change_password),
                            icon = Icons.Outlined.Lock,
                            onClick = { onEvent(UserSettingsUIEvents.ToggleChangePasswordDialog(true)) }
                        )
                    }
                }
            }
            
            item {
                SettingsCategoryHeader("Aplikacja")
            }
            
            item {
                val notificationsEnabled = remember { mutableStateOf(false) }
                SettingsSwitchRow(
                    title = "Powiadomienia (wkrótce)",
                    icon = Icons.Outlined.Notifications,
                    checked = notificationsEnabled.value,
                    onCheckedChange = { notificationsEnabled.value = it }
                )
            }

            if (!state.isAnonymous) {
                item {
                    SettingsCategoryHeader("Inne")
                }
                
                item {
                    SettingsItemRow(
                        title = stringResource(R.string.title_delete_account),
                        icon = Icons.Outlined.Delete,
                        onClick = { onEvent(UserSettingsUIEvents.ToggleDeleteAccountDialog(true)) }
                    )
                }

                item {
                    SettingsItemRow(
                        title = stringResource(R.string.btn_logout),
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        onClick = {
                            onEvent.invoke(UserSettingsUIEvents.SignOut)
                            onSignOut()
                        }
                    )
                }
            }
        }
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
            if (userEmail.isNotBlank()) {
                TextCaption(text = userEmail)
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

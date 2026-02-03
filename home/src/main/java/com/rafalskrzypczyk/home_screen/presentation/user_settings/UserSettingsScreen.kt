package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ConfirmationDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.SettingsCategoryHeader
import com.rafalskrzypczyk.core.composables.SettingsDialog
import com.rafalskrzypczyk.core.composables.SettingsItemRow
import com.rafalskrzypczyk.core.composables.SettingsSwitchRow
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.home.R

@Composable
fun UserSettingsScreen(
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    onTermsOfService: () -> Unit,
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
        contentWindowInsets = WindowInsets(0,0,0,0),
        topBar = {
            NavTopBar(title = stringResource(R.string.title_settings)) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))

        when(state.responseState) {
            is ResponseState.Error -> ErrorDialog(state.responseState.message) { onEvent(UserSettingsUIEvents.ClearState) }
            else -> {
                Box(modifier = modifier.fillMaxSize()) {
                    UserSettingsContent(
                        state = state,
                        onEvent = onEvent,
                        onSignOut = onSignOut,
                        onTermsOfService = onTermsOfService
                    )
                    
                    if (state.responseState == ResponseState.Loading) {
                        Loading()
                    }
                }
            }
        }
    }

    LaunchedEffect(state.responseState, state.successConfirmAction) {
        if (state.responseState is ResponseState.Success) {
            if (state.successConfirmAction == UserSettingsConfirmAction.NAVIGATE_OUT) {
                onSignOut()
            }
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

    if (state.showDeleteProgressDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.title_delete_progress),
            message = stringResource(R.string.text_delete_progress_warning),
            onConfirm = { onEvent(UserSettingsUIEvents.DeleteProgress) },
            onDismiss = { onEvent(UserSettingsUIEvents.ToggleDeleteProgressDialog(false)) }
        )
    }
}

@Composable
private fun UserSettingsContent(
    modifier: Modifier = Modifier,
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onSignOut: () -> Unit,
    onTermsOfService: () -> Unit,
) {
    Column (modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
            )
        ) {
            if (!state.isAnonymous) {
                item {
                    UserSettingsUserDetails(
                        userName = if(state.isAnonymous) stringResource(R.string.user_anonymous) else state.userName,
                        userEmail = state.userEmail,
                        isPremium = state.isPremium
                    )
                }

                item {
                    SettingsCategoryHeader(stringResource(R.string.settings_category_account))
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
                SettingsCategoryHeader(stringResource(R.string.settings_category_app))
            }
            
            item {
                val notificationsEnabled = remember { mutableStateOf(false) }
                SettingsSwitchRow(
                    title = stringResource(R.string.settings_notifications_soon),
                    icon = Icons.Outlined.Notifications,
                    checked = notificationsEnabled.value,
                    onCheckedChange = { notificationsEnabled.value = it }
                )
            }

            item {
                SettingsCategoryHeader(stringResource(R.string.settings_category_data))
            }

            item {
                SettingsItemRow(
                    title = stringResource(R.string.title_delete_progress),
                    icon = Icons.Outlined.DeleteForever,
                    onClick = { onEvent(UserSettingsUIEvents.ToggleDeleteProgressDialog(true)) }
                )
            }

            item {
                SettingsCategoryHeader(stringResource(R.string.settings_category_other))
            }

            item {
                SettingsItemRow(
                    title = stringResource(R.string.terms_of_service_title),
                    icon = Icons.Outlined.Description,
                    onClick = onTermsOfService
                )
            }

            if (!state.isAnonymous) {
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
    }
}

@Composable
private fun UserSettingsUserDetails(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String,
    isPremium: Boolean = false
) {
    Row (
        modifier = modifier
            .padding(horizontal = Dimens.DEFAULT_PADDING)
            .padding(vertical = Dimens.SMALL_PADDING)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val borderModifier = if (isPremium) {
            Modifier.border(Dimens.OUTLINE_THICKNESS, MQYellow, CircleShape)
        } else {
            Modifier
        }

        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.avatar_default),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(Dimens.IMAGE_SIZE_SMALL)
                .clip(CircleShape)
                .aspectRatio(1f)
                .then(borderModifier)
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
private fun UserNonPasswordSettingsPreview() {
    PreviewContainer {
        UserSettingsScreen(
            state = UserSettingsState(
                userName = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_name),
                userEmail = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_email)
            ),
            onEvent = {},
            onNavigateBack = {},
            onSignOut = {},
            onTermsOfService = {}
        )
    }
}

@Composable
@Preview
private fun UserPasswordSettingsPreview() {
    PreviewContainer {
        UserSettingsScreen(
            state = UserSettingsState(
                accountType = UserAuthenticationMethod.PASSWORD,
                userName = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_name),
                userEmail = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_email)
            ),
            onEvent = {},
            onNavigateBack = {},
            onSignOut = {},
            onTermsOfService = {}
        )
    }
}

@Composable
@Preview
private fun UserAnonymousSettingsPreview() {
    PreviewContainer {
        UserSettingsScreen(
            state = UserSettingsState(
                isAnonymous = true,
                userName = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_name),
                userEmail = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_email)
            ),
            onEvent = {},
            onNavigateBack = {},
            onSignOut = {},
            onTermsOfService = {}
        )
    }
}

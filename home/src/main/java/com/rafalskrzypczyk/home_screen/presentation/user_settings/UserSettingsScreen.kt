package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ConfirmationDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.InfoDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.SettingsCategoryCard
import com.rafalskrzypczyk.core.composables.SettingsCategoryHeader
import com.rafalskrzypczyk.core.composables.SettingsDialog
import com.rafalskrzypczyk.core.composables.SettingsItemRow
import com.rafalskrzypczyk.core.composables.SettingsSwitchRow
import com.rafalskrzypczyk.core.composables.TestBuildBanner
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TimePickerDialog
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.notifications.NotificationPermission
import com.rafalskrzypczyk.notifications.NotificationSettings

@Composable
fun UserSettingsScreen(
    state: UserSettingsState,
    onEvent: (UserSettingsUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    onTermsOfService: () -> Unit,
    onPrivacyPolicy: () -> Unit
) {
    val context = LocalContext.current
    val successMsg = stringResource(com.rafalskrzypczyk.core.R.string.desc_success)

    val appVersion = remember {
        runCatching {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${PackageInfoCompat.getLongVersionCode(packageInfo)})"
        }.getOrNull().orEmpty()
    }
    var showAboutDialog by remember { mutableStateOf(false) }

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
                        onTermsOfService = onTermsOfService,
                        onPrivacyPolicy = onPrivacyPolicy,
                        onAbout = { showAboutDialog = true }
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

    if (showAboutDialog) {
        InfoDialog(
            title = stringResource(R.string.about_app_title),
            message = stringResource(R.string.about_app_description) +
                    "\n\n" +
                    stringResource(R.string.about_app_version, appVersion),
            icon = Icons.Outlined.Info,
            onDismiss = { showAboutDialog = false }
        )
    }

    if (state.showTimePickerDialog) {
        TimePickerDialog(
            title = stringResource(R.string.title_reminder_time),
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            onConfirm = { hour, minute -> onEvent(UserSettingsUIEvents.SetReminderTime(hour, minute)) },
            onDismiss = { onEvent(UserSettingsUIEvents.ToggleTimePickerDialog(false)) }
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
    onPrivacyPolicy: () -> Unit,
    onAbout: () -> Unit
) {
    val context = LocalContext.current
    var systemEnabled by remember { mutableStateOf(NotificationPermission.areNotificationsEnabled(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        systemEnabled = NotificationPermission.areNotificationsEnabled(context)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        systemEnabled = NotificationPermission.areNotificationsEnabled(context)
        if (granted) onEvent(UserSettingsUIEvents.SetNotificationsEnabled(true))
    }

    val onNotificationsToggle: (Boolean) -> Unit = { enabled ->
        if (enabled) {
            onEvent(UserSettingsUIEvents.SetNotificationsEnabled(true))
            if (!systemEnabled) {
                val permissionNotGranted = NotificationPermission.requiresRuntimePermission() &&
                    ContextCompat.checkSelfPermission(
                        context,
                        NotificationPermission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                if (permissionNotGranted) {
                    notificationPermissionLauncher.launch(NotificationPermission.POST_NOTIFICATIONS)
                } else {
                    // Zablokowane na poziomie systemu — kierujemy do ustawień systemowych.
                    NotificationSettings.openAppNotificationSettings(context)
                }
            }
        } else {
            onEvent(UserSettingsUIEvents.SetNotificationsEnabled(false))
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .heightIn(min = maxHeight),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(bottom = Dimens.DEFAULT_PADDING)
            ) {
                if (!state.isAnonymous) {
                    UserSettingsUserDetails(
                        userName = state.userName,
                        userEmail = state.userEmail,
                        isPremium = state.isPremium
                    )

                    SettingsCategoryHeader(stringResource(R.string.settings_category_account))

                    SettingsCategoryCard {
                        SettingsItemRow(
                            title = stringResource(R.string.title_change_username),
                            icon = Icons.Outlined.Badge,
                            onClick = { onEvent(UserSettingsUIEvents.ToggleChangeUsernameDialog(true)) }
                        )

                        if (state.accountType == UserAuthenticationMethod.PASSWORD) {
                            SettingsItemRow(
                                title = stringResource(R.string.title_change_password),
                                icon = Icons.Outlined.Lock,
                                onClick = { onEvent(UserSettingsUIEvents.ToggleChangePasswordDialog(true)) }
                            )
                        }
                    }
                }

                SettingsCategoryHeader(stringResource(R.string.settings_category_app))

                SettingsCategoryCard {
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_notifications),
                        icon = Icons.Outlined.Notifications,
                        checked = state.notificationsEnabled && systemEnabled,
                        onCheckedChange = onNotificationsToggle
                    )

                    if (state.notificationsEnabled) {
                        SettingsItemRow(
                            title = stringResource(R.string.settings_reminder_time),
                            icon = Icons.Outlined.Schedule,
                            value = formatReminderTime(state.reminderHour, state.reminderMinute),
                            showChevron = false,
                            onClick = { onEvent(UserSettingsUIEvents.ToggleTimePickerDialog(true)) }
                        )
                    }

                    SettingsItemRow(
                        title = stringResource(R.string.settings_system_notifications),
                        icon = Icons.Outlined.Settings,
                        onClick = { NotificationSettings.openAppNotificationSettings(context) }
                    )

                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_sound),
                        icon = Icons.Outlined.VolumeUp,
                        checked = state.soundEnabled,
                        onCheckedChange = { onEvent(UserSettingsUIEvents.SetSoundEnabled(it)) }
                    )

                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_haptic),
                        icon = Icons.Outlined.Vibration,
                        checked = state.hapticEnabled,
                        onCheckedChange = { onEvent(UserSettingsUIEvents.SetHapticEnabled(it)) }
                    )
                }

                SettingsCategoryHeader(stringResource(R.string.settings_category_data))

                SettingsCategoryCard {
                    SettingsItemRow(
                        title = stringResource(R.string.title_delete_progress),
                        icon = Icons.Outlined.DeleteForever,
                        onClick = { onEvent(UserSettingsUIEvents.ToggleDeleteProgressDialog(true)) }
                    )
                }

                SettingsCategoryHeader(stringResource(R.string.settings_category_other))

                SettingsCategoryCard {
                    SettingsItemRow(
                        title = stringResource(R.string.terms_of_service_title),
                        icon = Icons.Outlined.Description,
                        onClick = onTermsOfService
                    )

                    SettingsItemRow(
                        title = stringResource(R.string.privacy_policy),
                        icon = Icons.Outlined.Description,
                        onClick = onPrivacyPolicy
                    )

                    SettingsItemRow(
                        title = stringResource(R.string.settings_about_app),
                        icon = Icons.Outlined.Info,
                        onClick = onAbout
                    )

                    if (!state.isAnonymous) {
                        SettingsItemRow(
                            title = stringResource(R.string.title_delete_account),
                            icon = Icons.Outlined.Delete,
                            onClick = { onEvent(UserSettingsUIEvents.ToggleDeleteAccountDialog(true)) }
                        )

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

            Column {
                TestBuildBanner(
                    modifier = Modifier.padding(bottom = Dimens.DEFAULT_PADDING)
                )

                BrandingElement(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(vertical = Dimens.DEFAULT_PADDING)
                )
            }
        }
    }
}

private fun formatReminderTime(hour: Int, minute: Int): String =
    "%02d:%02d".format(hour, minute)

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
            onTermsOfService = {},
            onPrivacyPolicy = {}
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
            onTermsOfService = {},
            onPrivacyPolicy = {}
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
            onTermsOfService = {},
            onPrivacyPolicy = {}
        )
    }
}

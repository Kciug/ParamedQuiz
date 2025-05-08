package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rafalskrzypczyk.home_screen.presentation.home_screen.MainMenuScreen
import com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page.UserPageScreen
import com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page.UserPageVM
import com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings.UserSettingsScreen
import com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings.UserSettingsVM
import com.rafalskrzypczyk.main_mode.navigation.MainModeNavHost
import com.rafalskrzypczyk.signup.SignupNavHost
import kotlinx.serialization.Serializable

@Serializable
object Signup

fun NavGraphBuilder.signupDestination(
    onExit: () -> Unit,
    onAuthenticated: () -> Unit
) {
    composable<Signup> {
        SignupNavHost(
            onExitPressed = onExit,
            onSignupFinished = onAuthenticated,
        )
    }
}

fun NavController.navigateToSignup() {
    navigate(route = Signup) {
        popUpTo<UserPage> { inclusive = true }
    }
}

@Serializable
object MainMenu

fun NavGraphBuilder.mainMenuDestination(
    onNavigateToUserPanel: () -> Unit,
    onNavigateToMainMode: () -> Unit
) {
    composable<MainMenu> {
        MainMenuScreen(
            onNavigateToUserPanel = { onNavigateToUserPanel() },
            onNavigateToMainMode = { onNavigateToMainMode() }
        )
    }
}

@Serializable
object UserPage

fun NavGraphBuilder.userPageDestination(
    onNavigateBack: () -> Unit,
    onUserSettings: () -> Unit,
) {
    composable<UserPage> {
        val viewModel = hiltViewModel<UserPageVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        UserPageScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onUserSettings = onUserSettings,
        )
    }
}

fun NavController.navigateToUserPage(){
    navigate(
        route = UserPage
    ) {
        popUpTo<Signup> { inclusive = true }
    }
}

@Serializable
object UserSettings

fun NavGraphBuilder.userSettingsDestination(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    composable<UserSettings> {
        val viewModel = hiltViewModel<UserSettingsVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        UserSettingsScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onSignOut = onSignOut,
        )
    }
}

fun NavController.navigateToUserSettings() {
    navigate(route = UserSettings)
}

@Serializable
object MainMode

fun NavGraphBuilder.mainModeDestination(
    onExit: () -> Unit,
    onUserPanel: () -> Unit
) {
    composable<MainMode> {
        MainModeNavHost(
            onExit = onExit,
            onUserPanel = onUserPanel
        )
    }
}

fun NavController.navigateToMainMode() {
    navigate(route = MainMode)
}
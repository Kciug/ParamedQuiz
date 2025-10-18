package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rafalskrzypczyk.home_screen.presentation.home_page.HomeScreen
import com.rafalskrzypczyk.home_screen.presentation.home_page.HomeScreenVM
import com.rafalskrzypczyk.home_screen.presentation.onboarding.OnboardingScreen
import com.rafalskrzypczyk.home_screen.presentation.onboarding.OnboardingVM
import com.rafalskrzypczyk.home_screen.presentation.user_page.UserPageScreen
import com.rafalskrzypczyk.home_screen.presentation.user_page.UserPageVM
import com.rafalskrzypczyk.home_screen.presentation.user_settings.UserSettingsScreen
import com.rafalskrzypczyk.home_screen.presentation.user_settings.UserSettingsVM
import com.rafalskrzypczyk.main_mode.navigation.MainModeNavHost
import com.rafalskrzypczyk.main_mode.presentation.daily_exercise.DailyExerciseVM
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizScreen
import com.rafalskrzypczyk.paramedquiz.dev.DevOptionsScreen
import com.rafalskrzypczyk.paramedquiz.dev.DevVM
import com.rafalskrzypczyk.signup.SignupNavHost
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeScreen
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeVM
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
object DailyExercise

fun NavGraphBuilder.dailyExerciseDestination(
    onNavigateBack: () -> Unit
) {
    composable<DailyExercise> {
        val viewModel = hiltViewModel<DailyExerciseVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        MMQuizScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToDailyExercise() {
    navigate(route = DailyExercise)
}

@Serializable
object MainMenu

fun NavGraphBuilder.mainMenuDestination(
    onNavigateToUserPanel: () -> Unit,
    onNavigateToDailyExercise: () -> Unit,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: () -> Unit,
    onNavigateToDev: () -> Unit
) {
    composable<MainMenu> {
        val viewModel = hiltViewModel<HomeScreenVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        HomeScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateToUserPanel = onNavigateToUserPanel,
            onNavigateToDailyExercise = onNavigateToDailyExercise,
            onNavigateToMainMode = onNavigateToMainMode,
            onNavigateToSwipeMode = onNavigateToSwipeMode,
            onNavigateToDevOptions = onNavigateToDev

        )
    }
}

fun NavController.navigateToMainMenu() {
    navigate(route = MainMenu) {
        popUpTo(0)
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

@Serializable
object SwipeMode

fun NavGraphBuilder.swipeModeDestination(
    onNavigateBack: () -> Unit
) {
    composable<SwipeMode> {
        val viewModel = hiltViewModel<SwipeModeVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        SwipeModeScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToSwipeMode() {
    navigate(route = SwipeMode)
}

@Serializable
object Onboarding

fun NavGraphBuilder.onboardingDestination(
    navigateToSignup: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    composable<Onboarding> {
        val viewModel = hiltViewModel<OnboardingVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        OnboardingScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            navigateToLogin = navigateToSignup,
            onFinishOnboarding = onFinishOnboarding
        )
    }
}

@Serializable
object Dev

fun NavGraphBuilder.devDestination(
    navigateBack: () -> Unit
) {
    composable<Dev> {
        val viewModel = hiltViewModel<DevVM>()

        DevOptionsScreen(
            onEvent = viewModel::onEvent,
            onNavigateBack = navigateBack
        )
    }
}

fun NavController.navigateToDevOptions() {
    navigate(route = Dev)
}
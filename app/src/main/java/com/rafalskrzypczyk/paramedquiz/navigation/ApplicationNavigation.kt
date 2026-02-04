package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rafalskrzypczyk.home_screen.presentation.home_page.HomeScreen
import com.rafalskrzypczyk.home_screen.presentation.home_page.HomeScreenVM
import com.rafalskrzypczyk.home_screen.presentation.onboarding.OnboardingScreen
import com.rafalskrzypczyk.home_screen.presentation.onboarding.OnboardingVM
import com.rafalskrzypczyk.home_screen.presentation.terms_of_service.TermsOfServiceScreen
import com.rafalskrzypczyk.home_screen.presentation.terms_of_service.TermsOfServiceVM
import com.rafalskrzypczyk.home_screen.presentation.user_page.UserPageScreen
import com.rafalskrzypczyk.home_screen.presentation.user_page.UserPageVM
import com.rafalskrzypczyk.home_screen.presentation.user_settings.UserSettingsScreen
import com.rafalskrzypczyk.home_screen.presentation.user_settings.UserSettingsVM
import androidx.compose.runtime.remember
import com.rafalskrzypczyk.main_mode.navigation.MainModeNavHost
import com.rafalskrzypczyk.main_mode.presentation.MainModeEntryVM
import com.rafalskrzypczyk.translation_mode.navigation.TranslationModeNavHost
import com.rafalskrzypczyk.translation_mode.presentation.TranslationModeEntryVM
import com.rafalskrzypczyk.main_mode.presentation.daily_exercise.DailyExerciseVM
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizScreen
import com.rafalskrzypczyk.paramedquiz.dev.DevOptionsScreen
import com.rafalskrzypczyk.paramedquiz.dev.DevVM
import com.rafalskrzypczyk.signup.SignupNavHost
import com.rafalskrzypczyk.swipe_mode.navigation.SwipeModeNavHost
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeEntryVM
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

fun NavController.navigateToSignup(popUpToUserPage: Boolean = false) {
    navigate(route = Signup) {
        if(popUpToUserPage) {
            popUpTo<UserPage> { inclusive = true }
        }
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
    onNavigateToTranslationMode: () -> Unit,
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
            onNavigateToTranslationMode = onNavigateToTranslationMode,
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
    onSignup: () -> Unit
) {
    composable<UserPage> {
        val viewModel = hiltViewModel<UserPageVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        UserPageScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onOpenSettings = onUserSettings,
            onOpenRegistration = onSignup
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
    onTermsOfService: () -> Unit,
) {
    composable<UserSettings> {
        val viewModel = hiltViewModel<UserSettingsVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        UserSettingsScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onSignOut = onSignOut,
            onTermsOfService = onTermsOfService
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
        val viewModel = hiltViewModel<MainModeEntryVM>()
        val showOnboarding = remember { viewModel.shouldShowOnboarding() }

        MainModeNavHost(
            onExit = onExit,
            onUserPanel = onUserPanel,
            showOnboarding = showOnboarding
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
        val viewModel = hiltViewModel<SwipeModeEntryVM>()
        val showOnboarding = remember { viewModel.shouldShowOnboarding() }

        SwipeModeNavHost(
            onExit = onNavigateBack,
            showOnboarding = showOnboarding
        )
    }
}

fun NavController.navigateToSwipeMode() {
    navigate(route = SwipeMode)
}

@Serializable
object TranslationMode

fun NavGraphBuilder.translationModeDestination(
    onNavigateBack: () -> Unit
) {
    composable<TranslationMode> {
        val viewModel = hiltViewModel<TranslationModeEntryVM>()
        val showOnboarding = remember { viewModel.shouldShowOnboarding() }
        
        TranslationModeNavHost(
            onExit = onNavigateBack,
            showOnboarding = showOnboarding
        )
    }
}

fun NavController.navigateToTranslationMode() {
    navigate(route = TranslationMode)
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

@Serializable
data class TermsOfService(val isMandatory: Boolean = true)

fun NavGraphBuilder.termsOfServiceDestination(
    onAccepted: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<TermsOfService> {
        val viewModel = hiltViewModel<TermsOfServiceVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        val route = it.toRoute<TermsOfService>()

        TermsOfServiceScreen(
            state = state.value,
            isMandatory = route.isMandatory,
            onEvent = viewModel::onEvent,
            onAccepted = onAccepted,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToTermsOfService(isMandatory: Boolean = true) {
    navigate(route = TermsOfService(isMandatory = isMandatory)) {
        if (isMandatory) {
            popUpTo(0)
        }
    }
}
package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rafalskrzypczyk.home_screen.presentation.home_screen.MainMenuScreen
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

fun NavController.navigateToSignup(){
    navigate(
        route = Signup
    )
}

@Serializable
object MainMenu

fun NavGraphBuilder.mainMenuDestination(
    onNavigateToUserPanel: () -> Unit
) {
    composable<MainMenu> {
        MainMenuScreen(
            onNavigateToUserPanel = { onNavigateToUserPanel() }
        )
    }
}
package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rafalskrzypczyk.home_screen.presentation.home_screen.MainMenuScreen
import com.rafalskrzypczyk.signup.SignupNav
import kotlinx.serialization.Serializable

fun NavController.navigateToSignup(){
    navigate(
        route = SignupNav
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
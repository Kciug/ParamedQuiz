package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rafalskrzypczyk.home_screen.presentation.home_screen.MainMenuScreen
import kotlinx.serialization.Serializable

@Serializable
object MainMenu

fun NavGraphBuilder.mainMenuDestination() {
    composable<MainMenu> {
        MainMenuScreen()
    }
}
package com.rafalskrzypczyk.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.rafalskrzypczyk.signup.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object SignupNav

fun NavGraphBuilder.signupNavGraph(navController: NavHostController) {
    navigation<SignupNav>(startDestination = Login) {
        loginDestination(navController)
    }
}

@Serializable
object Login

fun NavGraphBuilder.loginDestination(
    navController: NavHostController
) {
    composable<Login> {
        LoginScreen(
            onUserAuthenticated = {},
            onNavigateBack = { navController.popBackStack() },
            onResetPassword = {}
        )
    }
}


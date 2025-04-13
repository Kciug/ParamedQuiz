package com.rafalskrzypczyk.signup

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.signup.login.LoginScreen
import com.rafalskrzypczyk.signup.register.RegisterScreen
import kotlinx.serialization.Serializable

@Composable
fun SignupNavHost(
    onExitPressed: () -> Unit,
    onSignupFinished: () -> Unit,
) {
    val signupNavController = rememberNavController()

    NavHost(
        navController = signupNavController,
        startDestination = Login
    ) {
        loginDestination(
            onUserAuthenticated = onSignupFinished,
            onExitPressed = onExitPressed,
            onResetPassword = {},
            onRegister = { signupNavController.navigateToRegister() }
        )

        registerDestination(
            onBackPressed = { signupNavController.popBackStack() },
            onExitPressed = onExitPressed,
            onUserAuthenticated = onSignupFinished
        )
    }
}

@Serializable
object Login

fun NavGraphBuilder.loginDestination(
    onUserAuthenticated: () -> Unit,
    onExitPressed: () -> Unit,
    onResetPassword: () -> Unit,
    onRegister: () -> Unit,
) {
    composable<Login> {
        LoginScreen(
            onUserAuthenticated = onUserAuthenticated,
            onNavigateBack = onExitPressed,
            onResetPassword = onResetPassword,
            onRegister = onRegister
        )
    }
}

@Serializable
object Register

fun NavGraphBuilder.registerDestination(
    onBackPressed: () -> Unit,
    onExitPressed: () -> Unit,
    onUserAuthenticated: () -> Unit,
) {
    composable<Register> {
        RegisterScreen(
            onUserAuthenticated = onUserAuthenticated,
            onNavigateBack = onBackPressed,
            onExitPressed = onExitPressed,
        )
    }
}

fun NavController.navigateToRegister() {
    navigate(
        route = Register
    )
}


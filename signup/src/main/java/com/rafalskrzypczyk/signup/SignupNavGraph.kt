package com.rafalskrzypczyk.signup

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.signup.login.LoginScreen
import com.rafalskrzypczyk.signup.register.RegisterScreen
import com.rafalskrzypczyk.signup.reset_password.ResetPasswordScreen
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
            onResetPassword = { signupNavController.navigateToResetPassword() },
            onRegister = { signupNavController.navigateToRegister() }
        )

        registerDestination(
            onBackPressed = { signupNavController.popBackStack() },
            onExitPressed = onExitPressed,
            onUserAuthenticated = onSignupFinished
        )

        resetPasswordDestination(
            onBackPressed = { signupNavController.popBackStack() },
            onExitPressed = onExitPressed,
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

@Serializable
object ResetPassword

fun NavGraphBuilder.resetPasswordDestination(
    onBackPressed: () -> Unit,
    onExitPressed: () -> Unit,
) {
    composable<ResetPassword> {
        ResetPasswordScreen(
            onNavigateBack = onBackPressed,
            onExitPressed = onExitPressed,
        )
    }
}

fun NavController.navigateToResetPassword() {
    navigate(
        route = ResetPassword
    )
}


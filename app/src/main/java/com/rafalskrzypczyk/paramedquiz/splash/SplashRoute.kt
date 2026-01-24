package com.rafalskrzypczyk.paramedquiz.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SplashRoute(
    viewModel: SplashVM,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToTermsOfService: () -> Unit,
    onNavigateToMainMenu: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                SplashNavigationEvent.NavigateToOnboarding -> onNavigateToOnboarding()
                SplashNavigationEvent.NavigateToTermsOfService -> onNavigateToTermsOfService()
                SplashNavigationEvent.NavigateToMainMenu -> onNavigateToMainMenu()
            }
        }
    }
}
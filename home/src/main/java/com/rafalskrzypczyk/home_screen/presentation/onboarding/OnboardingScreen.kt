package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.PreviewContainer

@Composable
fun OnboardingScreen(
    navigateToLogin: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    var moveToOnboarding by rememberSaveable { mutableStateOf(false) }

    AnimatedContent(
        targetState = moveToOnboarding,
        label = "Onboarding",
        transitionSpec = {
            fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
        }
    ) { onboardingStarted ->
        if (onboardingStarted) {
            OnboardingSequence(
                onBackToWelcomePage = { moveToOnboarding = false },
                navigateToLogin = navigateToLogin,
                onFinish = onFinishOnboarding
            )
        } else {
            OnboardingWelcomePage(
                onStartClick = { moveToOnboarding = true },
                navigateToLogin = navigateToLogin
            )
        }
    }
}

@Composable
@Preview
private fun OnboardingScreenPreview() {
    PreviewContainer {
        OnboardingScreen(
            navigateToLogin = {},
            onFinishOnboarding = {}
        )
    }
}
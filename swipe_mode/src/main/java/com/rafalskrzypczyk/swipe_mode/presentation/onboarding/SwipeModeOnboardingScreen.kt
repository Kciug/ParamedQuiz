package com.rafalskrzypczyk.swipe_mode.presentation.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingHeader
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingIconComposition
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingPage
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingShell
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.swipe_mode.R

@Composable
fun SwipeModeOnboardingScreen(
    onNavigateBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            OnboardingPage(
                title = stringResource(R.string.SM_ob_page_1_title),
                message = stringResource(R.string.SM_ob_page_1_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.SwapHoriz,
                        mainIconColor = MaterialTheme.colorScheme.primary,
                        secondaryIcons = listOf(Icons.Default.TouchApp)
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.SM_ob_page_2_title),
                message = stringResource(R.string.SM_ob_page_2_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Timer,
                        mainIconColor = MQYellow,
                        secondaryIcons = listOf(Icons.Default.FlashOn)
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.SM_ob_page_3_title),
                message = stringResource(R.string.SM_ob_page_3_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Whatshot,
                        mainIconColor = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    )

    OnboardingShell(
        pages = pages,
        onFinish = onFinishOnboarding,
        onBack = onNavigateBack,
        header = {
            OnboardingHeader(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                icon = Icons.Default.Gesture
            )
        }
    )
}

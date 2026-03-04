package com.rafalskrzypczyk.cem_mode.presentation.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingHeader
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingIconComposition
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingPage
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingShell
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.cem_mode.R

@Composable
fun CemOnboardingScreen(
    onNavigateBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            OnboardingPage(
                title = stringResource(R.string.cem_ob_page_1_title),
                message = stringResource(R.string.cem_ob_page_1_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Style,
                        mainIconColor = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.cem_ob_page_2_title),
                message = stringResource(R.string.cem_ob_page_2_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Category,
                        mainIconColor = MQYellow
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.cem_ob_page_3_title),
                message = stringResource(R.string.cem_ob_page_3_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.AutoMirrored.Filled.MenuBook,
                        mainIconColor = MQGreen
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
                title = stringResource(R.string.title_cem_mode),
                icon = Icons.Default.School
            )
        }
    )
}

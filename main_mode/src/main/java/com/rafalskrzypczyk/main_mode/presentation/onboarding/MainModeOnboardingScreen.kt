package com.rafalskrzypczyk.main_mode.presentation.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingHeader
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingIconComposition
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingPage
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingShell
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.main_mode.R

@Composable
fun MainModeOnboardingScreen(
    onNavigateBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            OnboardingPage(
                title = stringResource(R.string.MM_ob_page_1_title),
                message = stringResource(R.string.MM_ob_page_1_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Outlined.Style,
                        mainIconColor = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.MM_ob_page_2_title),
                message = stringResource(R.string.MM_ob_page_2_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Rounded.QuestionMark,
                        mainIconColor = MQYellow
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.MM_ob_page_3_title),
                message = stringResource(R.string.MM_ob_page_3_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Outlined.BarChart,
                        mainIconColor = MQGreen,
                        secondaryIcons = listOf(Icons.AutoMirrored.Filled.TrendingUp)
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
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
                icon = Icons.Outlined.School
            )
        }
    )
}

package com.rafalskrzypczyk.translation_mode.presentation.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingHeader
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingIconComposition
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingPage
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingShell
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.translation_mode.R

@Composable
fun TranslationModeOnboardingScreen(
    onNavigateBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            OnboardingPage(
                title = stringResource(R.string.TM_ob_page_1_title),
                message = stringResource(R.string.TM_ob_page_1_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Translate,
                        mainIconColor = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.TM_ob_page_2_title),
                message = stringResource(R.string.TM_ob_page_2_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Edit,
                        mainIconColor = MQYellow
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.TM_ob_page_3_title),
                message = stringResource(R.string.TM_ob_page_3_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Psychology,
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
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
                icon = Icons.Default.Language
            )
        }
    )
}

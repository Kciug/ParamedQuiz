package com.rafalskrzypczyk.home_screen.presentation.privacy_consent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextCaptionLink
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingIconComposition
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.home.R

/**
 * Jednorazowe pytanie o zgodę na analitykę i diagnostykę awarii, pokazywane po akceptacji
 * regulaminu (a użytkownikom sprzed aktualizacji — przy pierwszym uruchomieniu).
 *
 * Dwa jawne przyciski i żadnego wyjścia bokiem: gest wstecz jest przechwytywany, bo brak decyzji
 * zostawiłby stan nierozstrzygnięty, a ekran wróciłby przy następnym starcie.
 */
@Composable
fun PrivacyConsentScreen(
    onEvent: (PrivacyConsentUIEvents) -> Unit,
    onDecided: () -> Unit,
) {
    BackHandler(enabled = true) { }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .testTag(TestTags.PRIVACY_CONSENT_ROOT)
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingIconComposition(
                mainIcon = Icons.Outlined.Insights,
                mainIconColor = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(Dimens.LARGE_PADDING))

            TextHeadline(
                text = stringResource(R.string.privacy_consent_title),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))

            TextPrimary(
                text = stringResource(R.string.privacy_consent_message),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))

            TextCaptionLink(
                text = stringResource(R.string.privacy_policy),
                url = stringResource(R.string.privacy_policy_url),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Dimens.LARGE_PADDING))

            ButtonPrimary(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.privacy_consent_accept),
                onClick = {
                    onEvent(PrivacyConsentUIEvents.Grant)
                    onDecided()
                }
            )

            Spacer(Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            ButtonTertiary(
                title = stringResource(R.string.privacy_consent_decline),
                onClick = {
                    onEvent(PrivacyConsentUIEvents.Deny)
                    onDecided()
                }
            )
        }
    }
}

@Composable
@Preview
private fun PrivacyConsentScreenPreview() {
    PreviewContainer {
        PrivacyConsentScreen(onEvent = {}, onDecided = {})
    }
}

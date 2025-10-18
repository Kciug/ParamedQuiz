package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

@Composable
fun OnboardingSequence(
    onBackToWelcomePage: () -> Unit,
    navigateToLogin: () -> Unit,
    onFinish: () -> Unit
) {
    Scaffold { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        ButtonPrimary(
            modifier = modifier,
            title = stringResource(R.string.ob_btn_finish),
            onClick = onFinish
        )
    }
}

@Composable
fun OnboardingSequencePage(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    icon: ImageVector
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.LARGE_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(Dimens.IMAGE_SIZE_SMALL)
                    .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)
                )
            )
            Spacer(Modifier.width(Dimens.ELEMENTS_SPACING))
            TextHeadline(title)
        }
        Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
        TextPrimary(message)
    }
}

@Preview
@Composable
private fun OnboardingSequencePagePreview() {
    PreviewContainer {
        OnboardingSequencePage(
            title = "Żurawie",
            message = "Wczesnym rankiem, gdy mgła unosi się nad wilgotnymi łąkami, słychać charakterystyczny klangor żurawi. " +
                    "Te dostojne ptaki, o rozpiętości skrzydeł przekraczającej dwa metry, od wieków fascynują obserwatorów przyrody. " +
                    "Wędrują tysiące kilometrów, by powrócić w te same miejsca lęgowe, z zadziwiającą dokładnością godną zegarmistrza",
            icon = Icons.Default.Flight
        )
    }
}



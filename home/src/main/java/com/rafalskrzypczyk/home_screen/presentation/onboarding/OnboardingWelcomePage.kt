package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaptionLink
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.home.R

@Composable
fun OnboardingWelcomePage(
    onStartClick: () -> Unit,
    navigateToLogin: () -> Unit
) {
    Scaffold { innerPadding ->
        val modifier = Modifier.padding(
            top = 0.dp,
            bottom = innerPadding.calculateBottomPadding(),
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
        )

        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(com.rafalskrzypczyk.core.R.drawable.mediquiz_ic_grayscale),
                contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(
                            bottomStart = Dimens.RADIUS_DEFAULT,
                            bottomEnd = Dimens.RADIUS_DEFAULT
                        )
                    )
                    .statusBarsPadding()
            )
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(Dimens.DEFAULT_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
            ) {
                TextTitle(stringResource(com.rafalskrzypczyk.core.R.string.app_name))
                TextCaptionLink(
                    text = stringResource(R.string.frontfolks_website),
                    url = stringResource(R.string.frontfolks_url),
                    textDecoration = TextDecoration.None
                )
                ButtonPrimary(
                    title = stringResource(R.string.ob_btn_start),
                    onClick = onStartClick
                )
                ButtonTertiary(
                    title = stringResource(R.string.ob_btn_navigate_to_login),
                    onClick = navigateToLogin
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.DEFAULT_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextCaptionLink(
                    text = stringResource(R.string.terms_of_service),
                    url = stringResource(R.string.terms_of_service_url)
                )
                TextCaptionLink(
                    text = stringResource(R.string.privacy_policy),
                    url = stringResource(R.string.privacy_policy_url)
                )
            }
        }
    }
}
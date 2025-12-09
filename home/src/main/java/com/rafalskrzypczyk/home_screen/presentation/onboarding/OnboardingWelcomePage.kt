package com.rafalskrzypczyk.home_screen.presentation.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaptionLink
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.home.R

@Composable
fun OnboardingWelcomePage(
    isUserLogged: Boolean,
    onStartClick: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)

        if(isLandscape) {
            OnboardingWelcomePageLandscape(
                modifier = modifier,
                isUserLogged = isUserLogged,
                onStartClick = onStartClick,
                navigateToLogin = navigateToLogin
            )
        } else {
            OnboardingWelcomePagePortrait(
                modifier = modifier,
                isUserLogged = isUserLogged,
                onStartClick = onStartClick,
                navigateToLogin = navigateToLogin
            )
        }
    }
}

@Composable
fun OnboardingWelcomePageContent(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    isUserLogged: Boolean,
    onStartClick: () -> Unit,
    navigateToLogin: () -> Unit
) {


    var contentModifier = modifier

    if(isLandscape) {
        contentModifier = contentModifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(
                    topStart = Dimens.RADIUS_DEFAULT,
                    bottomStart = Dimens.RADIUS_DEFAULT
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Right))
            .padding(
                horizontal = Dimens.DEFAULT_PADDING,
                vertical = Dimens.LARGE_PADDING
            )
    } else {
        contentModifier = contentModifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(
                    topStart = Dimens.RADIUS_DEFAULT,
                    topEnd = Dimens.RADIUS_DEFAULT
                )
            )
            .padding(
                horizontal = Dimens.DEFAULT_PADDING,
                vertical = Dimens.LARGE_PADDING
            )
    }

    Column(
        modifier = contentModifier,
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
        if (!isUserLogged) {
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

@Composable
fun OnboardingWelcomePagePortrait(
    modifier: Modifier = Modifier,
    isUserLogged: Boolean,
    onStartClick: () -> Unit,
    navigateToLogin: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.mediquiz_ic_grayscale),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.app_name),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(
                        bottomStart = Dimens.RADIUS_DEFAULT,
                        bottomEnd = Dimens.RADIUS_DEFAULT
                    )
                )
        )
        OnboardingWelcomePageContent(
            isUserLogged = isUserLogged,
            onStartClick = onStartClick,
            navigateToLogin = navigateToLogin
        )
    }
}

@Composable
fun OnboardingWelcomePageLandscape(
    modifier: Modifier = Modifier,
    isUserLogged: Boolean,
    onStartClick: () -> Unit,
    navigateToLogin: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.mediquiz_ic_grayscale),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.app_name),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .weight(1f)
                .background(color = MaterialTheme.colorScheme.primary)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Left))
        )
        OnboardingWelcomePageContent(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            isLandscape = true,
            isUserLogged = isUserLogged,
            onStartClick = onStartClick,
            navigateToLogin = navigateToLogin
        )
    }
}
package com.rafalskrzypczyk.home_screen.presentation.onboarding

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingIconComposition
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingPage
import com.rafalskrzypczyk.core.composables.onboarding.OnboardingShell
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.home.R

@Composable
fun OnboardingSequence(
    state: OnboardingState,
    onBackToWelcomePage: () -> Unit,
    navigateToLogin: () -> Unit,
    onFinish: () -> Unit,
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            OnboardingPage(
                title = stringResource(R.string.ob_page_1_title),
                message = stringResource(R.string.ob_page_1_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Emergency,
                        mainIconColor = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.ob_page_2_title),
                message = stringResource(R.string.ob_page_2_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.AutoMirrored.Filled.MenuBook,
                        mainIconColor = MQYellow,
                        secondaryIcons = listOf(Icons.Default.Science, Icons.Default.MedicalServices)
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.ob_page_3_title),
                message = stringResource(R.string.ob_page_3_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Dashboard,
                        mainIconColor = MaterialTheme.colorScheme.secondary,
                        secondaryIcons = listOf(Icons.Default.History,
                            Icons.AutoMirrored.Filled.TrendingUp
                        )
                    )
                }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.ob_page_4_title),
                message = stringResource(R.string.ob_page_4_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.VerifiedUser,
                        mainIconColor = MQGreen,
                        secondaryIcons = listOf(Icons.Default.Gavel,
                            Icons.AutoMirrored.Filled.FactCheck
                        )
                    )
                }
            )
        },
        {
            OnboardingSequenceLoginPage(
                state = state,
                onNavigateToRegister = { navigateToLogin() }
            )
        },
        {
            OnboardingPage(
                title = stringResource(R.string.ob_page_end_title),
                message = stringResource(R.string.ob_page_end_message),
                iconCompose = {
                    OnboardingIconComposition(
                        mainIcon = Icons.Default.Celebration,
                        mainIconColor = MQYellow
                    )
                }
            )
        },
    )

    OnboardingShell(
        pages = pages,
        onFinish = onFinish,
        onBack = onBackToWelcomePage,
        skipButtonText = stringResource(R.string.ob_btn_skip),
        nextButtonText = stringResource(R.string.ob_btn_next),
        finishButtonText = stringResource(R.string.ob_btn_finish),
        showSkipButton = { pageIndex ->
             pageIndex < pages.size - 1 && !(pageIndex == 4 && !state.isLogged)
        }
    )
}

@Composable
fun OnboardingSequenceLoginPage(
    state: OnboardingState,
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val headlineText = if(state.isLogged) state.userName else stringResource(R.string.ob_page_login_title)
    val messageText = if(state.isLogged) state.userEmail else stringResource(R.string.ob_page_login_message)

    if(isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.LARGE_PADDING),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnboardingLoginAvatar(isPremium = state.isPremium)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextHeadline(headlineText)
                TextPrimary(
                    text = messageText,
                    textAlign = TextAlign.Center
                )
                if(state.isLogged) {
                    OnboardingSequenceLoginPageLoginSuccessfullyBadge()
                } else {
                    ButtonPrimary(
                        title = stringResource(R.string.ob_page_login_btn),
                        onClick = onNavigateToRegister
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.LARGE_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
        ) {
            OnboardingLoginAvatar(isPremium = state.isPremium)
            TextHeadline(headlineText)
            TextPrimary(
                text = messageText,
                textAlign = TextAlign.Center
            )
            if(state.isLogged) {
                OnboardingSequenceLoginPageLoginSuccessfullyBadge()
            } else {
                ButtonPrimary(
                    title = stringResource(R.string.ob_page_login_btn),
                    onClick = onNavigateToRegister
                )
            }
        }
    }
}

@Composable
fun OnboardingLoginAvatar(
    isPremium: Boolean
) {
    val borderModifier = if (isPremium) {
        Modifier.border(Dimens.OUTLINE_THICKNESS, MQYellow, CircleShape)
    } else {
        Modifier
    }

    Box {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.avatar_default),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.Transparent)
                .then(borderModifier)
                .size(Dimens.IMAGE_SIZE)
        )

        if (isPremium) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = Dimens.SMALL_PADDING)
                    .background(MQYellow, RoundedCornerShape(Dimens.RADIUS_SMALL))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(Dimens.RADIUS_SMALL)
                    )
                    .padding(horizontal = Dimens.SMALL_PADDING, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(com.rafalskrzypczyk.core.R.string.premium_badge),
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingSequenceLoginPageLoginSuccessfullyBadge() {
    Row(
        modifier = Modifier
            .animateContentSize()
            .padding(Dimens.DEFAULT_PADDING)
            .background(
                color = MQGreen,
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)
            )
            .padding(Dimens.DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircleOutline,
            contentDescription = null,
            tint = Color.Black
        )
        TextPrimary(
            text = stringResource(R.string.ob_page_end_logged_successfully),
            color = Color.Black,
            modifier = Modifier.padding(
                start = Dimens.ELEMENTS_SPACING_SMALL,
                end = Dimens.DEFAULT_PADDING
            )
        )
    }
}

@Preview
@Composable
private fun OnboardingSequencePreview() {
    PreviewContainer {
        OnboardingPage(
            title = "Żurawie",
            message = "Wczesnym rankiem, gdy mgła unosi się nad wilgotnymi łąkami, słychać charakterystyczny klangor żurawi. " +
                    "Te dostojne ptaki, o rozpiętości skrzydeł przekraczającej dwa metry, od wieków fascynują obserwatorów przyrody. " +
                    "Wędrują tysiące kilometrów, by powrócić w te same miejsca lęgowe, z zadziwiającą dokładnością godną zegarmistrza",
            iconCompose = {
                OnboardingIconComposition(
                    mainIcon = Icons.Default.Emergency,
                    mainIconColor = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}



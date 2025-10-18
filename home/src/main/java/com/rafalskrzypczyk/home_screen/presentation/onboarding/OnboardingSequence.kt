package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    var currentPage by remember { mutableIntStateOf(0) }
    var movedToNext by remember { mutableStateOf(false) }

    var moveNextAutomatically by remember { mutableStateOf(false) }

    var continueButtonText = stringResource(R.string.ob_btn_next)

    val pages = listOf<@Composable () -> Unit>(
        {
            OnboardingSequencePage(
                title = stringResource(R.string.ob_page_1_title),
                message = stringResource(R.string.ob_page_1_message),
                icon = Icons.Default.Emergency
            )
        },
        {
            OnboardingSequencePage(
                title = stringResource(R.string.ob_page_2_title),
                message = stringResource(R.string.ob_page_2_message),
                icon = Icons.Default.QuestionMark
            )
        },
        {
            OnboardingSequencePage(
                title = stringResource(R.string.ob_page_3_title),
                message = stringResource(R.string.ob_page_3_message),
                icon = Icons.Default.ForkRight
            )
        },
        {
            OnboardingSequencePage(
                title = stringResource(R.string.ob_page_4_title),
                message = stringResource(R.string.ob_page_4_message),
                icon = Icons.Default.Favorite
            )
        },
        {
            OnboardingSequenceLoginPage (
                onNavigateToRegister = {
                    moveNextAutomatically = true
                    navigateToLogin()
                },
                nextButtonTitleCallback = { title ->
                    continueButtonText = title
                }
            )
        },
        {
            OnboardingSequencePage(
                title = stringResource(R.string.ob_page_end_title),
                message = stringResource(R.string.ob_page_end_message),
                icon = Icons.Default.Celebration,
                nextButtonTitle = stringResource(R.string.ob_btn_finish),
                nextButtonTitleCallback = { title ->
                    continueButtonText = title
                }
            )
        },
    )

    val onClickNext = {
        if(currentPage == pages.size - 1) {
            onFinish()
        } else {
            movedToNext = true
            currentPage++
        }
    }

    val onBackClick = {
        if (currentPage == 0) {
            onBackToWelcomePage()
        } else {
            movedToNext = false
            currentPage--
        }
    }

    LaunchedEffect(moveNextAutomatically) {
        if (moveNextAutomatically) {
            moveNextAutomatically = false
            onClickNext()
        }
    }

    Scaffold { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            AnimatedBackButton(showText = currentPage == 0) { onBackClick() }

            AnimatedContent(
                targetState = currentPage,
                modifier = Modifier.align(Alignment.Center),
                label = "currentMode",
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if(movedToNext) it else -it }
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { if(movedToNext) -it else it }
                    )
                }
            ) { pageIndex ->
                pages[pageIndex].invoke()
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardingPageIndicator(
                    pagesCount = pages.size,
                    currentPage = currentPage
                )
                Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
                ButtonPrimary(
                    title = continueButtonText,
                    onClick = { onClickNext() }
                )
            }
        }
    }
}

@Composable
fun AnimatedBackButton(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back"
            )
        }
        AnimatedVisibility(
            visible = showText,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            TextPrimary(
                text = stringResource(com.rafalskrzypczyk.core.R.string.btn_back),
                modifier = Modifier.padding(
                    start = Dimens.ELEMENTS_SPACING_SMALL,
                    end = Dimens.DEFAULT_PADDING
                )
            )
        }
    }
}

@Composable
fun OnboardingPageIndicator(
    modifier: Modifier = Modifier,
    pagesCount: Int,
    currentPage: Int,
    dotSize: Dp = Dimens.NOTIFICATION_DOT,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING),
        modifier = modifier
    ) {
        repeat(pagesCount) { index ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(
                        color = if (index == currentPage) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun OnboardingSequencePage(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    icon: ImageVector,
    nextButtonTitle: String? = null,
    nextButtonTitleCallback: (String) -> Unit = {}
) {
    if(nextButtonTitle != null) nextButtonTitleCallback(nextButtonTitle)

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
        TextPrimary(
            text = message,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OnboardingSequenceLoginPage(
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit,
    nextButtonTitleCallback: (String) -> Unit
) {
    nextButtonTitleCallback(stringResource(R.string.ob_btn_skip))

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.LARGE_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.avatar_default),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.Transparent)
                .size(Dimens.IMAGE_SIZE)
        )
        TextHeadline(stringResource(R.string.ob_page_login_title))
        TextPrimary(stringResource(R.string.ob_page_login_message))
        ButtonPrimary(
            title = stringResource(R.string.ob_page_login_btn),
            onClick = onNavigateToRegister
        )
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



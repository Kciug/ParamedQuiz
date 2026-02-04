package com.rafalskrzypczyk.core.composables.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary

@Composable
fun OnboardingShell(
    pages: List<@Composable () -> Unit>,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    skipButtonText: String = stringResource(R.string.btn_skip),
    nextButtonText: String = stringResource(R.string.btn_next),
    finishButtonText: String = stringResource(R.string.btn_finish),
    showSkipButton: (Int) -> Boolean = { it < pages.size - 1 },
    header: (@Composable () -> Unit)? = null
) {
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    var movedToNext by remember { mutableStateOf(false) }

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
            onBack()
        } else {
            movedToNext = false
            currentPage--
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    OnboardingBackButton(showText = currentPage == 0) { onBackClick() }

                    if (showSkipButton(currentPage)) {
                        ButtonTertiary(
                            title = skipButtonText,
                            onClick = { onFinish() },
                            fillMaxWidth = false,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }

                if (header != null) {
                    Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
                    header()
                }
            }

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
                    currentPage = currentPage,
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
                ButtonPrimary(
                    title = if (currentPage == pages.size - 1) finishButtonText else nextButtonText,
                    onClick = { onClickNext() }
                )
            }
        }
    }
}

@Composable
fun OnboardingBackButton(
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
                text = stringResource(R.string.btn_back),
                modifier = Modifier.padding(
                    start = Dimens.ELEMENTS_SPACING_SMALL,
                    end = Dimens.DEFAULT_PADDING
                )
            )
        }
    }
}

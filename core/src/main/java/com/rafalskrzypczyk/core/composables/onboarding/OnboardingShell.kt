package com.rafalskrzypczyk.core.composables.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { pages.size })

    val onClickNext = {
        if (pagerState.currentPage == pages.size - 1) {
            onFinish()
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    val onBackClick = {
        if (pagerState.currentPage == 0) {
            onBack()
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                OnboardingBackButton(showText = pagerState.currentPage == 0) { onBackClick() }

                if (showSkipButton(pagerState.currentPage)) {
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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                pages[pageIndex].invoke()
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardingPageIndicator(
                    pagesCount = pages.size,
                    currentPage = pagerState.currentPage,
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
                ButtonPrimary(
                    title = if (pagerState.currentPage == pages.size - 1) finishButtonText else nextButtonText,
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

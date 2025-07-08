package com.rafalskrzypczyk.core.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import kotlinx.coroutines.delay

@Composable
fun QuizFinishScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    var startProgressAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        startProgressAnimation = true
    }

    val animatedProgress: Float by animateFloatAsState(
        targetValue = if (startProgressAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "progress"
    )

    val animatedAlpha by animateFloatAsState(if (animatedProgress == 1f) 1f else 0f, label = "visibility")
    val animatedAlphaModifier = Modifier.alpha(animatedAlpha)

    Scaffold { innerPadding ->
        modifier.padding(innerPadding)

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
        ) {
            TextPrimary(
                text = stringResource(R.string.finish_title),
                modifier = animatedAlphaModifier
            )

            AnimatedContent(
                targetState = animatedAlpha,
                transitionSpec = {
                    scaleIn() togetherWith scaleOut()
                },
                label = "alpha"
            ) {
                if(it < 1f) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        progress = { animatedProgress },
                        strokeWidth = Dimens.OUTLINE_THICKNESS_LARGE,
                    )
                } else {
                    TextHeadline(
                        text = "100%",
                        modifier = animatedAlphaModifier
                    )
                }
            }

            ButtonSecondary(
                title = stringResource(R.string.btn_back),
                onClick = onNavigateBack,
                modifier = animatedAlphaModifier
            )
        }
    }
}

@Composable
@Preview
private fun QuizFinishScreenPreview() {
    ParamedQuizTheme {
        Scaffold { p ->
            QuizFinishScreen(
                modifier = Modifier.padding(p),
                onNavigateBack = {}
            )
        }
    }
}
package com.rafalskrzypczyk.core.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SequentiallyAnimatedColumn(
    modifier: Modifier = Modifier,
    enterDelay: Long = 500,
    delayBetween: Long = 300,
    contentSpacing: Dp = Dimens.ELEMENTS_SPACING,
    contentPadding: PaddingValues = PaddingValues(Dimens.DEFAULT_PADDING),
    content: List<@Composable () -> Unit>
) {
    val visibleStates = remember { content.map { mutableStateOf(false) } }

    LaunchedEffect(Unit) {
        delay(enterDelay)
        content.forEachIndexed { index, _ ->
            visibleStates[index].value = true
            delay(delayBetween)
        }
    }

    Column(
        modifier = modifier
    ) {
        content.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = visibleStates[index].value,
                enter = scaleIn(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ))
            ) {
                Box(modifier = Modifier.padding(
                    top = if (index == 0) contentPadding.calculateTopPadding() else contentSpacing,
                    bottom = if (index == content.size - 1) contentPadding.calculateBottomPadding() else 0.dp,
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
                )){ item() }
            }
        }
    }
}
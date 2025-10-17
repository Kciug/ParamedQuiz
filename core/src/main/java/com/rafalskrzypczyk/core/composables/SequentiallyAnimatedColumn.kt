package com.rafalskrzypczyk.core.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SequentiallyAnimatedColumn(
    modifier: Modifier = Modifier,
    enterDelay: Long = 500,
    delayBetween: Long = 300,
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.Top)
    ) {
        content.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = visibleStates[index].value,
                enter = scaleIn(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ))
            ) {
                item()
            }
        }
    }
}
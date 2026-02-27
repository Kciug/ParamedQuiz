package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import kotlinx.coroutines.delay

@Composable
fun AnswerResultIndicator(
    modifier: Modifier = Modifier,
    answerResult: SwipeModeAnswerResult,
    hideDelay: Long = 500,
    alphaModifier: Float = 0.2f,
    isLarge: Boolean = false,
    onAnimationFinished: () -> Unit = {}
) {
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(answerResult) {
        if(answerResult.result != SwipeQuizResult.NONE){
            showResult = true
            delay(if (isLarge) hideDelay * 2 else hideDelay)
            showResult = false
            if (isLarge) {
                delay(300) 
                onAnimationFinished()
            }
        }
    }

    val isCorrect = answerResult.result == SwipeQuizResult.CORRECT

    val icon = if(isCorrect) Icons.Default.Check else Icons.Default.Close

    val contentDescription = if(isCorrect) "Poprawna odpowiedź" else "Błędna odpowiedź"

    val color = if (isCorrect) MQGreen else MQRed

    val iconSize = if (isLarge) 120.dp else 24.dp
    val backgroundPadding = if (isLarge) Dimens.LARGE_PADDING else Dimens.DEFAULT_PADDING

    AnimatedVisibility(
        modifier = modifier,
        visible = showResult,
        enter = scaleIn(animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
        )) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        label = "answerResultVisibility"
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier
                .padding(Dimens.DEFAULT_PADDING)
                .background(
                    shape = CircleShape,
                    color = color.copy(alpha = alphaModifier)
                )
                .padding(backgroundPadding)
                .size(iconSize)
        )
    }
}

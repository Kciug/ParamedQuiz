package com.rafalskrzypczyk.swipe_mode.presentation

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.swipe_mode.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt

enum class SwipeDirection { Resting, Left, Right }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeQuestionCard(
    question: SwipeQuestionUIModel,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    horizontalPadding: Dp,
    onSubmit: (Long, Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    val dragState = remember {
        AnchoredDraggableState(
            initialValue = SwipeDirection.Resting,
            anchors = DraggableAnchors {
                SwipeDirection.Resting at 0f
                SwipeDirection.Left at -screenWidthPx
                SwipeDirection.Right at screenWidthPx
            },
            positionalThreshold = { d -> d * 0.7f },
            velocityThreshold = { 50f },
            snapAnimationSpec = tween(
                durationMillis = 200,
                easing = FastOutLinearInEasing
            ),
            decayAnimationSpec = decayAnimationSpec
        )
    }

    val buttonsAlpha by remember(dragState.requireOffset()) {
        derivedStateOf {
            val dragProgress = (dragState.requireOffset().absoluteValue / screenWidthPx * 2).coerceIn(0f, 1f)
            1f - dragProgress
        }
    }

    LaunchedEffect(dragState) {
        snapshotFlow { dragState.settledValue }
            .collectLatest {
                if (it == SwipeDirection.Right) onSubmit(question.id, true)
                if (it == SwipeDirection.Left) onSubmit(question.id, false)
            }
    }

    Box (
        modifier = Modifier
            .anchoredDraggable(
                state = dragState,
                orientation = Orientation.Horizontal,
            )
            .offset {
                val xOffset = dragState.requireOffset()

                val radius = 300f
                val yOffset =
                    radius - (radius * cos((xOffset / screenWidthPx) * Math.PI / 2)).toFloat()
                IntOffset(
                    x = xOffset.roundToInt(),
                    y = yOffset.roundToInt()
                )
            }
            .rotate(
                (dragState.requireOffset() / screenWidthPx) * 15f
            )
            .scale(
                lerp(
                    1f,
                    0.7f,
                    (dragState.requireOffset().absoluteValue / screenWidthPx).coerceIn(0f, 1f)
                )
            )
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .aspectRatio(1f)
            .shadow(
                elevation = Dimens.ELEVATION,
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
                clip = true
            )
            .background(containerColor)
            .padding(Dimens.DEFAULT_PADDING)
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .alpha(buttonsAlpha),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SwipeQuestionDecisionIconButton(
                onClick = {
                    coroutineScope.launch {
                        dragState.animateTo(targetValue = SwipeDirection.Left)
                    }
                },
                buttonIcon = Icons.Default.Close,
                buttonText = stringResource(R.string.swipe_button_false)
            )
            SwipeQuestionDecisionIconButton(
                onClick = {
                    coroutineScope.launch {
                        dragState.animateTo(targetValue = SwipeDirection.Right)
                    }
                },
                buttonIcon = Icons.Default.Check,
                buttonText = stringResource(R.string.swipe_button_true)
            )
        }
    }
}

@Composable
fun SwipeQuestionDecisionIconButton(
    onClick: () -> Unit,
    buttonIcon: ImageVector,
    buttonText: String,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = buttonText,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun SwipeQuizCard() {
    PreviewContainer {
        SwipeQuestionCard(
            question = SwipeQuestionUIModel(
                id = 0,
                text = "Czy xanax uzaleznia?"
            ),
            horizontalPadding = 0.dp,
            onSubmit = {_, _ -> }
        )
    }
}
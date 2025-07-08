package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ConfirmationDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.QuizFinishScreen
import com.rafalskrzypczyk.core.composables.QuizLinearProgressBar
import com.rafalskrzypczyk.core.ui.NavigationTopBar
import com.rafalskrzypczyk.swipe_mode.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt

@Composable
fun SwipeModeScreen(
    state: SwipeModeState,
    onEvent: (SwipeModeUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var showBackConfirmation by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    BackHandler {
        showBackConfirmation = true
    }

    LaunchedEffect(state.answerResult) {
        if(state.answerResult != SwipeQuizResult.NONE) {
            showResult = true
            delay(500)
            showResult = false
        }
    }

    Scaffold (
        topBar = {
            NavigationTopBar(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                onNavigateBack = { showBackConfirmation = true }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when(state.responseState) {
            ResponseState.Idle -> {}
            ResponseState.Loading -> Loading(modifier)
            is ResponseState.Error -> ErrorDialog(state.responseState.message) { onNavigateBack() }
            ResponseState.Success -> {
                Column (
                    modifier = modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuizLinearProgressBar(
                        progress = state.currentQuestionNumber,
                        range = state.questionsCount,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        state.questionsPair.forEach {
                            key(it.id) {
                                SwipeQuestionCard(
                                    question = it,
                                    onSubmit = { questionId, isCorrect ->
                                        onEvent.invoke(SwipeModeUIEvents.SubmitAnswer(questionId, isCorrect))
                                    }
                                )
                            }
                        }
                        this@Column.AnimatedVisibility(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(Dimens.DEFAULT_PADDING),
                            visible = showResult,
                            label = "answerResult",
                            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = if(state.answerResult == SwipeQuizResult.CORRECT) Icons.Default.Check else Icons.Default.Close,
                                tint = if(state.answerResult == SwipeQuizResult.CORRECT) Color.Green else Color.Red,
                                contentDescription = stringResource(R.string.ic_desc_result)
                            )
                        }
                    }
                    SwipeStatsComponent(
                        correctAnswers = state.correctAnswers,
                        currentStreak = state.currentStreak,
                        bestStreak = state.bestStreak
                    )
                }
            }
        }
    }

    if(state.isQuizFinished) {
        QuizFinishScreen(onNavigateBack = onNavigateBack)
    }

    if(showBackConfirmation) {
        ConfirmationDialog(
            title = stringResource(com.rafalskrzypczyk.core.R.string.dialog_title_confirm_exit_quiz),
            onConfirm = { onNavigateBack() },
            onDismiss = { showBackConfirmation = false }
        )
    }
}

enum class SwipeDirection { Resting, Left, Right }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeQuestionCard(
    question: SwipeQuestionUIModel,
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
                val yOffset = radius - (radius * cos((xOffset / screenWidthPx) * Math.PI / 2)).toFloat()
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
            .fillMaxWidth(0.95f)
            .aspectRatio(1f)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(10),
                clip = true
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(20.dp)
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = buttonText,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun SwipeStatsComponent(
    modifier: Modifier = Modifier,
    correctAnswers: Int,
    currentStreak: Int,
    bestStreak: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(30))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 20.dp)
    ) {
        val weightModifier = Modifier.weight(1f)

        SwipeStatsElement(
            modifier = weightModifier,
            title = stringResource(R.string.label_stats_streak),
            icon = Icons.Default.Bolt,
            value = currentStreak.toString()
        )
        SwipeStatsElement(
            modifier = weightModifier,
            title = stringResource(R.string.label_stats_correct_answers),
            icon = Icons.Default.Check,
            value = correctAnswers.toString()
        )
        SwipeStatsElement(
            modifier = weightModifier,
            title = stringResource(R.string.label_stats_best_streak),
            icon = Icons.Default.RocketLaunch,
            value = bestStreak.toString()
        )
    }
}

@Composable
fun SwipeStatsElement(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    value: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
            Text(value)
        }
        Text(title)
    }
}

@Composable
@Preview
private fun SwipeModeScreenPreview() {
    MaterialTheme {
        Surface {
            SwipeModeScreen(
                state = SwipeModeState(
                    questionsPair = listOf(
                        SwipeQuestionUIModel(0, "Przykładowe pytanie prawda/fałsz")
                    )
                ),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}
package com.rafalskrzypczyk.core.composables.quiz_finished

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.SequentiallyAnimatedColumn
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import kotlinx.coroutines.delay

@Composable
fun QuizFinishedScreen(
    state: QuizFinishedState,
    enterDelay: Long = 500,
    onNavigateBack: () -> Unit,
    extras: @Composable () -> Unit,
) {
    val backButtonVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(enterDelay)
        backButtonVisible.value = true
    }

    Scaffold(
        // Kluczowe dla Edge-to-Edge: ustawiamy insets na 0, aby tło weszło pod paski systemowe.
        // Paddingami zajmiemy się ręcznie wewnątrz.
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ -> // Ignorujemy padding ze Scaffolda
        
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // WARSTWA 1: Przewijalna zawartość
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Odstęp na pasek stanu (Status Bar)
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                // Główna zawartość
                SequentiallyAnimatedColumn(
                    modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
                    enterDelay = enterDelay,
                    content = listOf(
                        {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TextHeadline(stringResource(R.string.quiz_finish_title))
                                Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
                            }
                        },
                        { TextPrimary(stringResource(R.string.quiz_finish_your_answers)) },
                        {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                            ) {
                                TextHeadline(
                                    text = state.seenQuestions.toString(),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextPrimary(stringResource(R.string.quiz_finish_all_answers))
                            }
                        },
                        {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                            ) {
                                TextHeadline(
                                    text = state.correctAnswers.toString(),
                                    color = MQGreen
                                )
                                TextPrimary(stringResource(R.string.quiz_finish_correct_answers))
                            }
                        },
                        {
                            Row(
                                modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserPointsLabel(value = state.points)
                                Spacer(Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
                                TextPrimary(
                                    text = stringResource(
                                        R.string.quiz_finish_earned_points,
                                        state.earnedPoints
                                    ),
                                    color = MQYellow
                                )
                            }
                        },
                        { extras() }
                    )
                )

                // Odstęp na dole, aby ostatni element listy nie był schowany pod przyciskiem "Wróć"
                // Wysokość przycisku (~50dp) + Padding (15dp) + margines bezpieczeństwa
                Spacer(Modifier.height(80.dp))
                // Dodatkowy odstęp na pasek nawigacji (gestów)
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }

            // WARSTWA 2: Przycisk "Wróć" przyklejony do dołu
            AnimatedVisibility(
                visible = backButtonVisible.value,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    // Podnosimy przycisk nad pasek nawigacji systemowej
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(Dimens.DEFAULT_PADDING),
                enter = scaleIn(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ))
            ) {
                ButtonPrimary(
                    title = stringResource(R.string.btn_back),
                    onClick = onNavigateBack,
                )
            }
        }
    }
}

@Preview
@Composable
private fun QuizFinishedScreenPreview() {
    PreviewContainer {
        QuizFinishedScreen(
            state = QuizFinishedState(),
            onNavigateBack = {},
            extras = {}
        )
    }
}

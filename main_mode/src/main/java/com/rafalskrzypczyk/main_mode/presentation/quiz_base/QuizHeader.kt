package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.QuizLinearProgressBar
import com.rafalskrzypczyk.core.composables.TextScore
import com.rafalskrzypczyk.core.composables.UserPointsLabel

@Composable
fun QuizHeader (
    modifier: Modifier = Modifier,
    userScore: Int,
    correctAnswers: Int,
    totalQuestions: Int,
    currentQuestion: Int,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
    ) {
        Column (
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            QuizLinearProgressBar(
                modifier = Modifier.fillMaxWidth(),
                progress = currentQuestion,
                range = totalQuestions
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ){
                    UserPointsLabel(
                        value = userScore
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ){
                    CorrectAnswersAnimated(
                        correctAnswers = correctAnswers
                    )
                }
            }
        }
    }
}

@Composable
fun CorrectAnswersAnimated(
    modifier: Modifier = Modifier,
    correctAnswers: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.Green
        )
        AnimatedContent(
            targetState = correctAnswers,
            label = "CorrectAnswersAnimation",
            transitionSpec = {
                (slideInVertically { it } + scaleIn(initialScale = 0.8f)) togetherWith
                        (slideOutVertically { -it } + scaleOut(targetScale = 1.2f))
            },
            modifier = Modifier.padding(start = Dimens.ELEMENTS_SPACING_SMALL)
        ) { targetCount ->
            TextScore(
                text = targetCount.toString()
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun QuizHeaderPreview() {
    var correctAnswers by remember { mutableIntStateOf(0) }

    PreviewContainer {
        Column {
            QuizHeader(
                userScore = 100,
                correctAnswers = correctAnswers,
                totalQuestions = 10,
                currentQuestion = 5
            )
            ButtonPrimary(
                title = "Dodaj poprawną odpowiedź",
                onClick = { correctAnswers++ }
            )
        }
    }
}
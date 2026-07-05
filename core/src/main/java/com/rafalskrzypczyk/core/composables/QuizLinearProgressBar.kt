package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun QuizLinearProgressBar(
    modifier: Modifier = Modifier,
    progress: Int,
    range: Int,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedFraction by animateFloatAsState(
        targetValue = if (range > 0) (progress.toFloat() / range).coerceIn(0f, 1f) else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "quizProgressFractionAnimation"
    )

    val trackColor = progressColor.copy(alpha = 0.15f)

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = progress.toString(),
                color = progressColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = range.toString(),
                color = progressColor,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { animatedFraction },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = progressColor,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun QuizLinearProgressBarPreview() {
    ParamedQuizTheme {
        Surface {
            QuizLinearProgressBar(progress = 7, range = 23)
        }
    }
}
package com.rafalskrzypczyk.swipe_mode.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.BaseScoreLabel
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.swipe_mode.R

@Composable
fun SwipeStatsComponent(
    modifier: Modifier = Modifier,
    correctAnswers: Int,
    currentStreak: Int,
    bestStreak: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 20.dp)
    ) {
        val weightModifier = Modifier.weight(1f)

        SwipeStatsElement(
            modifier = weightModifier,
            title = stringResource(R.string.label_stats_streak),
            icon = Icons.Default.LocalFireDepartment,
            value = currentStreak
        )
        SwipeStatsElement(
            modifier = weightModifier,
            title = stringResource(R.string.label_stats_correct_answers),
            icon = Icons.Default.Check,
            iconColor = MQGreen,
            value = correctAnswers
        )
        SwipeStatsElement(
            modifier = weightModifier,
            title = stringResource(R.string.label_stats_best_streak),
            icon = Icons.Default.RocketLaunch,
            value = bestStreak
        )
    }
}

@Composable
fun SwipeStatsElement(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    value: Int,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BaseScoreLabel(
            value = value,
            icon = icon,
            color = iconColor,
            label = title,
            grayOutWhenZero = false
        )
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(
//                imageVector = icon,
//                contentDescription = title,
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Spacer(Modifier.width(10.dp))
//            Text(value)
//        }
        Text(title)
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun SwipeQuizCard() {
    PreviewContainer {
        SwipeStatsComponent(
            correctAnswers = 15,
            currentStreak = 3,
            bestStreak = 8
        )
    }
}
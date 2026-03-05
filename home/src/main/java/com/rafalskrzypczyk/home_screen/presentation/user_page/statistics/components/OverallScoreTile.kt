package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.home.R

@Composable
fun OverallScoreTile(
    modifier: Modifier = Modifier,
    score: Int,
    totalCorrect: Int,
    totalIncorrect: Int,
    totalUnique: Int,
    totalIdeal: Int
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextHeadline(text = stringResource(R.string.stats_overall))
                
                StatisticsChart(
                    modifier = Modifier.size(80.dp),
                    progress = score,
                    strokeWidth = 10.dp
                ) {
                    TextHeadline(
                        text = stringResource(R.string.percentage, score),
                        fontSize = 14.sp
                    )
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)) {
                    ScoreDetailItem(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.stats_summary_correct),
                        value = totalCorrect,
                        icon = Icons.Rounded.Check,
                        color = MQGreen
                    )
                    ScoreDetailItem(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.stats_summary_incorrect),
                        value = totalIncorrect,
                        icon = Icons.Rounded.Close,
                        color = MQRed
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)) {
                    ScoreDetailItem(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.stats_summary_unique),
                        value = totalUnique,
                        icon = Icons.Rounded.QuestionMark,
                        color = MaterialTheme.colorScheme.primary
                    )
                    ScoreDetailItem(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.stats_summary_ideal),
                        value = totalIdeal,
                        icon = Icons.Rounded.Star,
                        color = MQYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreDetailItem(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextHeadline(text = value.toString(), textAlign = TextAlign.Center)
                TextCaption(text = title, textAlign = TextAlign.Center)
            }
        }
    }
}

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.home.R

@Composable
fun ModeScoreTile(
    modifier: Modifier = Modifier,
    mode: QuizMode,
    title: String,
    score: Int,
    correctAnswers: Int,
    totalAnswers: Int,
    isAvailable: Boolean
) {
    val color = ModeInfoProvider.getColor(mode)
    val icon = ModeInfoProvider.getIcon(mode)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    TextHeadline(text = title)
                }

                if (isAvailable) {
                    TextCaption(
                        text = stringResource(R.string.stats_answers_count, correctAnswers, totalAnswers)
                    )
                } else {
                    TextCaption(text = stringResource(R.string.stats_no_data_for_mode_msg))
                }
            }

            if (isAvailable) {
                StatisticsChart(
                    modifier = Modifier.size(60.dp),
                    progress = score,
                    strokeWidth = 6.dp
                ) {
                    TextHeadline(
                        text = stringResource(R.string.percentage, score),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

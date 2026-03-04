package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats

@Composable
fun QuestionStatCard(
    modifier: Modifier = Modifier,
    question: QuestionWithStats,
    collapsedMaxLines: Int = 3
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "rotation")

    val scoreColor = when {
        question.correctPercentage >= 80 -> MQGreen
        question.correctPercentage >= 60 -> MQYellow
        else -> MQRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { isExpanded = !isExpanded },
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                StatisticsChart(
                    modifier = Modifier.size(48.dp),
                    progress = question.correctPercentage,
                    strokeWidth = 4.dp,
                    color = scoreColor
                ) {
                    TextCaption(
                        text = stringResource(R.string.percentage, question.correctPercentage),
                        color = scoreColor
                    )
                }

                TextPrimary(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    text = question.question,
                    maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines
                )

                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = stringResource(R.string.desc_expand),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .rotate(rotationState)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Dimens.DEFAULT_PADDING),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)) {
                        QuestionDetailRow(
                            icon = Icons.Rounded.Check,
                            iconColor = MQGreen,
                            label = stringResource(R.string.stats_summary_correct),
                            value = question.correctAnswers
                        )
                        QuestionDetailRow(
                            icon = Icons.Rounded.Close,
                            iconColor = MQRed,
                            label = stringResource(R.string.stats_summary_incorrect),
                            value = question.wrongAnswers
                        )
                        QuestionDetailRow(
                            icon = Icons.Rounded.QuestionMark,
                            iconColor = MaterialTheme.colorScheme.primary,
                            label = stringResource(R.string.stats_summary_unique),
                            value = question.correctAnswers + question.wrongAnswers
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionDetailRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            TextCaption(text = label)
        }
        TextHeadline(text = value.toString(), fontSize = 14.sp)
    }
}

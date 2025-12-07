package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

@Composable
fun NoEnoughDataComponent(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp = Dp.Unspecified,
    title: String,
    message: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
        )
        if(iconSize == Dp.Unspecified){
            TextPrimary(
                text = title,
                textAlign = TextAlign.Center
            )
            TextCaption(
                text = message,
                textAlign = TextAlign.Center
            )
        } else {
            TextHeadline(
                text = title,
                textAlign = TextAlign.Center
            )
            TextPrimary(
                text = message,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoStatisticsDataComponent(
    modifier: Modifier = Modifier,
) {
    NoEnoughDataComponent(
        modifier = modifier,
        icon = Icons.Rounded.Dashboard,
        iconSize = Dimens.IMAGE_SIZE_SMALL,
        title = stringResource(R.string.stats_no_data_overall),
        message = stringResource(R.string.stats_no_data_overall_msg)
    )
}

@Composable
fun NoStatisticsForModeComponent(
    modifier: Modifier = Modifier,
    modeTitle: String
) {
    NoEnoughDataComponent(
        modifier = modifier,
        icon = Icons.AutoMirrored.Default.HelpOutline,
        title = modeTitle,
        message = stringResource(R.string.stats_no_data_for_mode_msg)
    )
}

@Composable
fun NoBestWorstStatisticsComponent(
    modifier: Modifier = Modifier,
) {
    NoEnoughDataComponent(
        modifier = modifier,
        icon = Icons.AutoMirrored.Default.HelpOutline,
        title = stringResource(R.string.stats_no_best_worst_data),
        message = stringResource(R.string.stats_no_best_worst_data_msg)
    )
}
package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline

@Composable
fun StatisticsChart(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    progress: Int,
    numericalValueText: String? = null,
    numericalValueDescription: String? = null,
    strokeWidth: Dp = Dimens.STAT_BAR_WIDTH,
    content: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.5f else 1f)
                .aspectRatio(1f),
            progress = { progress / 100f },
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
            gapSize = -SliderDefaults.TrackStopIndicatorSize * strokeWidth.value
        )
        
        if (content != null) {
            content()
        } else if (numericalValueText != null || numericalValueDescription != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                numericalValueText?.let {
                    TextHeadline(text = it)
                }
                if (numericalValueText != null && numericalValueDescription != null) {
                    Spacer(Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                }
                numericalValueDescription?.let {
                    TextCaption(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        text = it,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

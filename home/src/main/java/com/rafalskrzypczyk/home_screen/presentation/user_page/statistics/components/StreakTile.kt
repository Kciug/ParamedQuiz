package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.home.R
import java.util.Calendar

@Composable
fun StreakTile(
    modifier: Modifier = Modifier,
    streak: Int,
    weeklyStreak: List<Boolean>
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = MQRed,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
                    TextTitle(text = streak.toString())
                }
                TextCaption(text = stringResource(R.string.stats_streak))
            }
            
            WeeklyStreakPreview(weeklyStreak = weeklyStreak)
        }
    }
}

@Composable
private fun WeeklyStreakPreview(
    weeklyStreak: List<Boolean>
) {
    val days = listOf(
        R.string.day_mon, R.string.day_tue, R.string.day_wed, 
        R.string.day_thu, R.string.day_fri, R.string.day_sat, R.string.day_sun
    )

    val todayIndex = remember {
        (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { index, dayRes ->
            val isDone = weeklyStreak.getOrNull(index) ?: false
            val isToday = index == todayIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
            ) {
                TextCaption(
                    text = stringResource(dayRes),
                    color = if (isToday) MQRed else Color.Unspecified,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )

                Box(
                    modifier = Modifier
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val statusModifier = if (isToday) {
                        Modifier
                            .size(38.dp)
                            .border(Dimens.OUTLINE_THICKNESS, MQRed, CircleShape)
                            .padding(4.dp)
                    } else {
                        Modifier.size(32.dp)
                    }

                    Box(
                        modifier = statusModifier
                            .clip(CircleShape)
                            .background(
                                if (isDone) MQRed else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

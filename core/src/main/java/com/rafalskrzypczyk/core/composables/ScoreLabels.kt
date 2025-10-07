package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow

@Composable
fun BaseScoreLabel(
    modifier: Modifier = Modifier,
    value: Int,
    icon: ImageVector,
    color: Color,
    label: String?,
    showNotification: Boolean = false
) {
    val color = if(value > 0 && !showNotification) color else Color.Gray

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
            if(showNotification) {
                NotificationDot(
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
        TextScore(
            modifier = Modifier.padding(start = Dimens.ELEMENTS_SPACING_SMALL),
            text = value.toString(),
        )
    }
}

@Composable
fun UserPointsLabel(
    modifier: Modifier = Modifier,
    value: Int,
) {
    BaseScoreLabel(
        modifier = modifier,
        value = value,
        icon = Icons.Rounded.Star,
        color = MQYellow,
        label = stringResource(R.string.desc_user_score)
    )
}

@Composable
fun UserStreakLabel(
    modifier: Modifier = Modifier,
    value: Int,
    isPending: Boolean
) {
    BaseScoreLabel(
        modifier = modifier,
        value = value,
        icon = Icons.Rounded.Bolt,
        color = MQRed,
        label = stringResource(R.string.desc_user_streak),
        showNotification = isPending
    )
}
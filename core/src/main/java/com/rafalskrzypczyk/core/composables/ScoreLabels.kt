package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R

@Composable
fun BaseScoreLabel(
    modifier: Modifier = Modifier,
    value: Int,
    icon: ImageVector,
    color: Color,
    label: String?
) {
    val color = if(value > 0) color else Color.Gray

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color
        )
        TextScore(
            text = value.toString(),
            color = color
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
        icon = Icons.Default.Star,
        color = MaterialTheme.colorScheme.primary,
        label = stringResource(R.string.desc_user_score)
    )
}

@Composable
fun UserStreakLabel(
    modifier: Modifier = Modifier,
    value: Int,
) {
    BaseScoreLabel(
        modifier = modifier,
        value = value,
        icon = Icons.Default.Bolt,
        color = MaterialTheme.colorScheme.error,
        label = stringResource(R.string.desc_user_streak)
    )
}
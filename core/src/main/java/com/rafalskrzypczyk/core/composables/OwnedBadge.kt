package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.ui.theme.adaptiveContentColor

@Composable
fun OwnedBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = Icons.Default.CheckCircle,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = backgroundColor.adaptiveContentColor(),
    isPending: Boolean = false
) {
    val finalBackgroundColor = if (isPending) MaterialTheme.colorScheme.secondaryContainer else backgroundColor
    val finalContentColor = if (isPending) finalBackgroundColor.adaptiveContentColor() else contentColor
    val finalIcon = if (isPending) Icons.Default.Timer else icon

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .background(finalBackgroundColor)
            .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = Dimens.SMALL_PADDING)
    ) {
        if (finalIcon != null) {
            Icon(
                imageVector = finalIcon,
                contentDescription = null,
                tint = finalContentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        TextPrimary(
            text = text,
            color = finalContentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

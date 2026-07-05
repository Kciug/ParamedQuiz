package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary

@Composable
fun RevisionsChoiceChip(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT),
        color = containerColor,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextPrimary(
                text = title,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.38f),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

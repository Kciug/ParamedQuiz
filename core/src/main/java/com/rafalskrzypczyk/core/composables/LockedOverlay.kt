package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R

@Composable
fun LockedOverlay(
    modifier: Modifier = Modifier,
    shape: Shape? = null
) {
    val finalModifier = if (shape != null) modifier.clip(shape) else modifier
    
    Box(
        modifier = finalModifier
            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(Dimens.IMAGE_SIZE_SMALL)
        )
    }
}

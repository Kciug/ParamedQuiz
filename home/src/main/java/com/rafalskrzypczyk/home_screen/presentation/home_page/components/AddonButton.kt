package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.NotificationDot
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

data class Addon(
    val title: String,
    val description: String? = null,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val highlighted: Boolean = false,
    val isAvailable: Boolean = true,
    val onClick: () -> Unit,
)

@Composable
fun AddonButton(
    modifier: Modifier = Modifier,
    addon: Addon,
) {
    Box(modifier = modifier.alpha(if (addon.isAvailable) 1f else 0.5f)) {
        Card(
            modifier = Modifier.defaultMinSize(minWidth = 150.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            onClick = rememberDebouncedClick(onClick = addon.onClick),
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.DEFAULT_PADDING),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = addon.iconBackgroundColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = addon.icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

                TextHeadline(
                    text = addon.title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                addon.description?.let {
                    TextPrimary(
                        text = it,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (addon.highlighted) {
            NotificationDot(
                size = Dimens.NOTIFICATION_DOT_LARGE,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.DEFAULT_PADDING)
            )
        }
    }
}

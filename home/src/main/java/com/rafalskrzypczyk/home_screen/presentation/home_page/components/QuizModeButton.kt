package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.LockedOverlay
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

@Composable
fun QuizModeButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    mode: QuizMode,
    locked: Boolean = false,
    onClick: () -> Unit
) {
    val icon = ModeInfoProvider.getIcon(mode)
    val iconBackgroundColor = ModeInfoProvider.getColor(mode)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = rememberDebouncedClick(onClick = onClick),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.DEFAULT_PADDING),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.DEFAULT_PADDING)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = iconBackgroundColor,
                            shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    TextHeadline(
                        text = title,
                        textAlign = TextAlign.Start
                    )
                    TextPrimary(
                        text = description,
                        textAlign = TextAlign.Start
                    )
                }
            }

            if (locked) {
                LockedOverlay(modifier = Modifier.matchParentSize())
            }
        }
    }
}

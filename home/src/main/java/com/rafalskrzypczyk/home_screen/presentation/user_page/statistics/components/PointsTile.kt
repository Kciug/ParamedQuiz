package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.home.R

@Composable
fun PointsTile(
    modifier: Modifier = Modifier,
    points: Int
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = MQYellow,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
                TextTitle(text = points.toString())
            }
            TextCaption(text = stringResource(R.string.stats_points))
        }
    }
}

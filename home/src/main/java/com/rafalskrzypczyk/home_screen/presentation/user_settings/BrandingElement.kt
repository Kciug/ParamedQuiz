package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R

private val HeartSize = 18.dp
private val HeartGlowSize = 34.dp

@Composable
fun BrandingElement(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextPrimary(
            text = stringResource(R.string.branding_made_with),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
        GlowingHeart()
        Spacer(Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
        TextPrimary(
            text = stringResource(R.string.branding_by_frontfolks),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GlowingHeart() {
    val heartColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.size(HeartGlowSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(HeartGlowSize)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                heartColor.copy(alpha = 0.55f),
                                heartColor.copy(alpha = 0f)
                            ),
                            center = center,
                            radius = size.minDimension / 2f
                        ),
                        radius = size.minDimension / 2f
                    )
                }
        )
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = stringResource(R.string.desc_branding_love),
            tint = heartColor,
            modifier = Modifier.size(HeartSize)
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun BrandingElementPreview() {
    ParamedQuizTheme {
        Surface {
            BrandingElement(modifier = Modifier.padding(vertical = Dimens.DEFAULT_PADDING))
        }
    }
}

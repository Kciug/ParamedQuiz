package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R

@Composable
fun BrandingElement(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextPrimary(
                text = stringResource(R.string.branding_created_with)
            )
            Spacer(Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.desc_branding_love),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256),
                contentDescription = stringResource(R.string.branding_frontfolks),
                Modifier.size(Dimens.IMAGE_SIZE_SMALL)
            )
            TextPrimary(
                text = stringResource(R.string.branding_frontfolks)
            )
        }
    }
}

@Composable
@Preview
private fun BrandingElementPreview() {
    ParamedQuizTheme {
        Surface {
            BrandingElement()
        }
    }
}
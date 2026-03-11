package com.rafalskrzypczyk.home_screen.presentation.store.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary

@Composable
fun StoreModeCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    price: String?,
    isUnlocked: Boolean,
    isPurchasing: Boolean,
    onBuyClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = Dimens.ELEVATION, shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .clip(RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            TextHeadline(
                text = title,
                modifier = Modifier.weight(1f)
            )
        }
        
        TextPrimary(text = description)
        
        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

        val buttonText = if (isUnlocked) {
            stringResource(com.rafalskrzypczyk.home.R.string.store_btn_purchased)
        } else if (price != null) {
            stringResource(com.rafalskrzypczyk.core.R.string.btn_buy_for, price)
        } else {
            stringResource(com.rafalskrzypczyk.home.R.string.store_btn_loading)
        }

        ButtonPrimary(
            title = buttonText,
            onClick = onBuyClick,
            enabled = !isUnlocked && price != null && !isPurchasing,
            loading = isPurchasing
        )
    }
}

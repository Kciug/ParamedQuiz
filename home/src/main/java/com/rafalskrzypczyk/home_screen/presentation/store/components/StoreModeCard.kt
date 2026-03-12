package com.rafalskrzypczyk.home_screen.presentation.store.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.OwnedBadge
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.adaptiveContentColor

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
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = iconTint,
                        shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint.adaptiveContentColor(),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
            ) {
                TextPrimary(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
                TextCaption(text = description)
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

        AnimatedContent(
            targetState = isUnlocked,
            label = "StoreModePurchaseAnimatedContent",
            modifier = Modifier.fillMaxWidth(),
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300)) using
                        SizeTransform { _, _ ->
                            tween(durationMillis = 300)
                        }
            }
        ) { unlocked ->
            if (unlocked) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OwnedBadge(
                        text = stringResource(com.rafalskrzypczyk.home.R.string.store_btn_purchased),
                        backgroundColor = MQGreen.copy(alpha = 0.2f),
                        contentColor = MQGreen
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (price != null) {
                        TextTitle(
                            text = price,
                            color = iconTint
                        )
                        TextCaption(text = stringResource(com.rafalskrzypczyk.core.R.string.one_time_purchase))
                        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                    }

                    val buttonText = if (price != null) {
                        stringResource(com.rafalskrzypczyk.core.R.string.btn_buy)
                    } else {
                        stringResource(com.rafalskrzypczyk.home.R.string.store_btn_loading)
                    }

                    ButtonPrimary(
                        title = buttonText,
                        onClick = onBuyClick,
                        enabled = price != null && !isPurchasing,
                        loading = isPurchasing
                    )
                }
            }
        }
    }
}

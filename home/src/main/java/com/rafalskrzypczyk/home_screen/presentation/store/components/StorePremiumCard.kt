package com.rafalskrzypczyk.home_screen.presentation.store.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQYellow

@Composable
fun StorePremiumCard(
    modifier: Modifier = Modifier,
    price: String?,
    isUnlocked: Boolean,
    isPurchasing: Boolean,
    onBuyClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GoldenPulse")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = MQYellow.copy(alpha = 0.5f),
        targetValue = MQYellow,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GoldenColorAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Dimens.ELEVATION,
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
                spotColor = animatedColor,
                ambientColor = animatedColor
            )
            .border(
                width = 2.dp,
                color = animatedColor,
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)
            )
            .clip(RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.DEFAULT_PADDING)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = MQYellow,
                    modifier = Modifier.size(32.dp)
                )
                TextHeadline(
                    text = stringResource(com.rafalskrzypczyk.home.R.string.premium_package_title),
                    modifier = Modifier.weight(1f)
                )
            }
            
            TextPrimary(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_package_desc))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
            ) {
                PremiumFeatureItem(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_feature_all_modes))
                PremiumFeatureItem(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_feature_all_categories))
                PremiumFeatureItem(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_feature_no_ads))
            }
            
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
}

@Composable
private fun PremiumFeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MQYellow,
            modifier = Modifier.size(20.dp)
        )
        TextPrimary(text = text, fontWeight = FontWeight.SemiBold)
    }
}

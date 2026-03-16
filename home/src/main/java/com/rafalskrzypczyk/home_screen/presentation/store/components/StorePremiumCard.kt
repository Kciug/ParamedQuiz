package com.rafalskrzypczyk.home_screen.presentation.store.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
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
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.ui.theme.adaptiveContentColor

@Composable
fun StorePremiumCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    price: String?,
    isUnlocked: Boolean,
    isPending: Boolean = false,
    isPurchasing: Boolean = false,
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

    val glowRadius = 16.dp
    val cornerRadius = Dimens.RADIUS_DEFAULT

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint()
                    val frameworkPaint = paint.asFrameworkPaint()
                    frameworkPaint.color = animatedColor.toArgb()
                    frameworkPaint.maskFilter = BlurMaskFilter(glowRadius.toPx(), BlurMaskFilter.Blur.NORMAL)
                    canvas.drawRoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = cornerRadius.toPx(),
                        radiusY = cornerRadius.toPx(),
                        paint = paint
                    )
                }
            }
            .clip(RoundedCornerShape(cornerRadius))
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
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MQYellow,
                            shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MQYellow.adaptiveContentColor()
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
            
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
            ) {
                PremiumFeatureItem(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_feature_all_modes))
                PremiumFeatureItem(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_feature_all_categories))
                PremiumFeatureItem(text = stringResource(com.rafalskrzypczyk.home.R.string.premium_feature_no_ads))
            }
            
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            AnimatedContent(
                targetState = isUnlocked to isPending,
                label = "PremiumPurchaseAnimatedContent",
                modifier = Modifier.fillMaxWidth(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300)) using
                            SizeTransform { _, _ ->
                                tween(durationMillis = 300)
                            }
                }
            ) { (unlocked, pending) ->
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
                } else if (pending) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OwnedBadge(
                            text = stringResource(com.rafalskrzypczyk.core.R.string.badge_pending),
                            isPending = true
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
                                color = MQYellow
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

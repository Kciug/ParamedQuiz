package com.rafalskrzypczyk.core.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.ui.theme.MQGreen

/**
 * Przyklejony dolny panel CTA zakupu: maszyna stanów kup / przetwarzanie / sukces / posiadane.
 * Współdzielony przez paywalle trybów ([BasePurchaseBottomSheet]) i panel zakupu kategorii —
 * jedno źródło prawdy dla logiki i wyglądu akcji zakupowej.
 */
@Composable
fun PurchaseActionSection(
    themeColor: Color,
    price: String?,
    purchaseError: String?,
    isPurchasing: Boolean,
    isUnlocked: Boolean,
    isPending: Boolean,
    isAlreadyUnlockedOnEntry: Boolean,
    onBuyClick: () -> Unit,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTryClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = Dimens.RADIUS_DEFAULT,
                    topEnd = Dimens.RADIUS_DEFAULT
                )
            )
            .padding(horizontal = Dimens.DEFAULT_PADDING)
            .padding(top = Dimens.LARGE_PADDING, bottom = Dimens.DEFAULT_PADDING)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        AnimatedContent(
            targetState = isUnlocked to isPending,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "purchaseSectionTransition"
        ) { (unlocked, pending) ->
            when {
                unlocked -> {
                    if (isAlreadyUnlockedOnEntry) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OwnedBadge(themeColor = themeColor)
                            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                            ButtonPrimary(
                                title = stringResource(R.string.btn_start),
                                onClick = onStartClick
                            )
                        }
                    } else {
                        SuccessSection(onStartClick = onStartClick)
                    }
                }

                pending -> {
                    PendingSection()
                }

                else -> {
                    PurchaseSection(
                        price = price,
                        purchaseError = purchaseError,
                        isPurchasing = isPurchasing,
                        onBuyClick = onBuyClick,
                        onTryClick = onTryClick,
                        themeColor = themeColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Loading(modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING))

        TextPrimary(
            text = stringResource(R.string.purchase_pending_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

        TextPrimary(
            text = stringResource(R.string.purchase_pending_msg),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Dimens.LARGE_PADDING)
        )
    }
}

@Composable
private fun SuccessSection(onStartClick: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.5f,
                stiffness = 200f
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MQGreen,
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = Dimens.ELEMENTS_SPACING)
                .scale(scale.value)
        )
        TextPrimary(
            text = stringResource(R.string.purchase_success_msg),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Dimens.LARGE_PADDING)
        )
        ButtonPrimary(
            title = stringResource(R.string.btn_start),
            onClick = onStartClick
        )
    }
}

@Composable
private fun PurchaseSection(
    price: String?,
    purchaseError: String?,
    isPurchasing: Boolean,
    onBuyClick: () -> Unit,
    onTryClick: (() -> Unit)?,
    themeColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextTitle(
            text = price ?: "---",
            color = themeColor
        )
        TextCaption(text = stringResource(R.string.one_time_purchase))

        if (purchaseError != null) {
            TextPrimary(
                text = purchaseError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING_SMALL),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

        ButtonPrimary(
            title = stringResource(R.string.btn_buy_for, price ?: "---"),
            onClick = onBuyClick,
            loading = isPurchasing,
            enabled = price != null
        )

        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

        onTryClick?.let { click ->
            ButtonSecondary(
                title = stringResource(R.string.btn_try),
                onClick = click
            )
        }
    }
}

@Composable
private fun OwnedBadge(themeColor: Color) {
    Surface(
        color = themeColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(Dimens.RADIUS_SMALL),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = themeColor,
                modifier = Modifier.size(16.dp)
            )
            TextPrimary(
                text = stringResource(R.string.badge_owned),
                color = themeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

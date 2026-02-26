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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import kotlinx.coroutines.launch

data class PurchaseModeDetails(
    val title: String,
    val description: String,
    val questionCount: Int,
    val price: String?,
    val features: List<PurchaseFeature>,
    val estimatedTime: String = "~5"
)

data class PurchaseFeature(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePurchaseBottomSheet(
    onDismiss: () -> Unit,
    details: PurchaseModeDetails,
    onBuyClick: () -> Unit,
    onStartClick: () -> Unit,
    onTryClick: (() -> Unit)? = null,
    isUnlocked: Boolean = false,
    isPurchasing: Boolean = false,
    purchaseError: String? = null,
    shouldDismiss: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val dismiss = {
        coroutineScope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    LaunchedEffect(shouldDismiss) {
        if (shouldDismiss) {
            dismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        sheetGesturesEnabled = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ExitButton(onClose = { dismiss() })
                TextTitle(
                    text = details.title,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            PremiumBadge()

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextPrimary(
                text = details.description,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING)
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextHeadline(
                text = stringResource(R.string.title_features),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            details.features.forEach { feature ->
                FeatureItem(feature)
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = details.questionCount.toString(),
                    label = stringResource(R.string.stat_questions),
                    icon = Icons.AutoMirrored.Filled.LibraryBooks
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = details.estimatedTime,
                    label = stringResource(R.string.stat_time),
                    icon = Icons.Default.Timer
                )
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            AnimatedContent(
                targetState = isUnlocked,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "purchaseSectionTransition"
            ) { unlocked ->
                if (unlocked) {
                    SuccessSection(onStartClick = onStartClick)
                } else {
                    PurchaseSection(
                        price = details.price,
                        purchaseError = purchaseError,
                        isPurchasing = isPurchasing,
                        onBuyClick = onBuyClick,
                        onTryClick = onTryClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))
        }
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
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MQGreen,
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = Dimens.ELEMENTS_SPACING)
                .scale(scale.value)
        )
        TextTitle(
            text = stringResource(R.string.purchase_success_title),
            color = MQGreen,
            modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING_SMALL)
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
    onTryClick: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextTitle(
            text = price ?: "---",
            color = MaterialTheme.colorScheme.primary
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
            loading = isPurchasing
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
private fun PremiumBadge() {
    Surface(
        color = Color(0xFFFFD700).copy(alpha = 0.2f),
        shape = RoundedCornerShape(Dimens.RADIUS_SMALL),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            TextPrimary(
                text = "PREMIUM",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun FeatureItem(feature: PurchaseFeature) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                TextPrimary(text = feature.title, fontWeight = FontWeight.Bold)
                TextCaption(text = feature.description)
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            TextScore(text = value, color = MaterialTheme.colorScheme.primary)
            TextCaption(text = label, textAlign = TextAlign.Center)
        }
    }
}

@Composable
@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
fun BasePurchaseBottomSheetPreview() {

}

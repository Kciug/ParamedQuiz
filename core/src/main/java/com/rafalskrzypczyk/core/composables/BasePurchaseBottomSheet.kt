package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.R
import kotlinx.coroutines.launch

data class PurchaseModeDetails(
    val title: String,
    val description: String,
    val questionCount: Int,
    val price: String?,
    val features: List<PurchaseFeature>,
    val estimatedTime: String = "~5 min/sesja"
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
    onTryClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val dismiss = {
        coroutineScope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { dismiss() },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
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

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            PremiumBadge()

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextPrimary(
                text = details.description,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING)
            )

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            TextHeadline(
                text = stringResource(R.string.title_features),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            details.features.forEach { feature ->
                FeatureItem(feature)
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = details.questionCount.toString(),
                    label = stringResource(R.string.stat_questions),
                    icon = null // Add icon if needed
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = details.estimatedTime,
                    label = stringResource(R.string.stat_time),
                    icon = null
                )
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            TextTitle(
                text = details.price ?: "---",
                color = MaterialTheme.colorScheme.primary
            )
            TextCaption(text = stringResource(R.string.one_time_purchase))

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            ButtonPrimary(
                title = stringResource(R.string.btn_buy_for, details.price ?: ""),
                onClick = onBuyClick
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            ButtonSecondary(
                title = stringResource(R.string.btn_try),
                onClick = onTryClick
            )

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))
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
    icon: ImageVector?
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
            TextTitle(text = value, textAlign = TextAlign.Center)
            TextCaption(text = label, textAlign = TextAlign.Center)
        }
    }
}

@Composable
@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
fun BasePurchaseBottomSheetPreview() {

}

package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.BasePurchaseBottomSheet
import com.rafalskrzypczyk.core.composables.PurchaseFeature
import com.rafalskrzypczyk.core.composables.PurchaseModeDetails
import com.rafalskrzypczyk.home.R

@Composable
fun TranslationPurchaseBottomSheet(
    isUnlocked: Boolean,
    isPurchasing: Boolean,
    purchaseError: String?,
    questionCount: Int,
    price: String?,
    activity: Activity?,
    shouldDismiss: Boolean,
    onDismiss: () -> Unit,
    onBuyClick: (Activity) -> Unit,
    onStartClick: () -> Unit
) {
    BasePurchaseBottomSheet(
        onDismiss = onDismiss,
        onBuyClick = { activity?.let { onBuyClick(it) } },
        onStartClick = onStartClick,
        isUnlocked = isUnlocked,
        isPurchasing = isPurchasing,
        purchaseError = purchaseError,
        shouldDismiss = shouldDismiss,
        details = PurchaseModeDetails(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
            description = stringResource(R.string.mode_translation_desc),
            questionCount = questionCount,
            price = price,
            features = listOf(
                PurchaseFeature(
                    title = stringResource(R.string.feature_translation_title),
                    description = stringResource(R.string.feature_translation_desc),
                    icon = Icons.Default.Translate
                ),
                PurchaseFeature(
                    title = stringResource(R.string.feature_vocabulary_title),
                    description = stringResource(R.string.feature_vocabulary_desc),
                    icon = Icons.Default.HistoryEdu
                ),
                PurchaseFeature(
                    title = stringResource(R.string.feature_auto_fix_title),
                    description = stringResource(R.string.feature_auto_fix_desc),
                    icon = Icons.Default.AutoFixHigh
                )
            )
        )
    )
}

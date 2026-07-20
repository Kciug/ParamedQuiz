package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.BasePurchaseBottomSheet
import com.rafalskrzypczyk.core.composables.PurchaseModeDetails
import com.rafalskrzypczyk.core.composables.modeDescription
import com.rafalskrzypczyk.core.composables.modeFeatures
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode

@Composable
fun SwipePurchaseBottomSheet(
    isUnlocked: Boolean,
    isPending: Boolean = false,
    isPurchasing: Boolean,
    purchaseError: String?,
    questionCount: Int,
    price: String?,
    activity: Activity?,
    shouldDismiss: Boolean,
    onDismiss: () -> Unit,
    onBuyClick: (Activity) -> Unit,
    onStartClick: () -> Unit,
    onTryClick: () -> Unit
) {
    BasePurchaseBottomSheet(
        onDismiss = onDismiss,
        onBuyClick = { activity?.let { onBuyClick(it) } },
        onStartClick = onStartClick,
        onTryClick = onTryClick,
        isUnlocked = isUnlocked,
        isPending = isPending,
        isPurchasing = isPurchasing,
        purchaseError = purchaseError,
        shouldDismiss = shouldDismiss,
        details = PurchaseModeDetails(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
            description = modeDescription(QuizMode.SwipeMode),
            questionCount = questionCount,
            price = price,
            icon = ModeInfoProvider.getIcon(QuizMode.SwipeMode),
            themeColor = ModeInfoProvider.getColor(QuizMode.SwipeMode),
            features = modeFeatures(QuizMode.SwipeMode)
        )
    )
}

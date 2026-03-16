package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.BasePurchaseBottomSheet
import com.rafalskrzypczyk.core.composables.PurchaseFeature
import com.rafalskrzypczyk.core.composables.PurchaseModeDetails
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.home.R

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
            description = stringResource(R.string.mode_swipe_desc),
            questionCount = questionCount,
            price = price,
            icon = ModeInfoProvider.getIcon(QuizMode.SwipeMode),
            themeColor = ModeInfoProvider.getColor(QuizMode.SwipeMode),
            features = listOf(
                PurchaseFeature(
                    title = stringResource(R.string.feature_swipe_title),
                    description = stringResource(R.string.feature_swipe_desc),
                    icon = Icons.Default.Swipe
                ),
                PurchaseFeature(
                    title = stringResource(R.string.feature_speed_title),
                    description = stringResource(R.string.feature_speed_desc),
                    icon = Icons.Default.Bolt
                ),
                PurchaseFeature(
                    title = stringResource(R.string.feature_combo_title),
                    description = stringResource(R.string.feature_combo_desc),
                    icon = Icons.Default.Whatshot
                )
            )
        )
    )
}

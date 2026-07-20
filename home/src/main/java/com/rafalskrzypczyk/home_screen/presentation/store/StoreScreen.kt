package com.rafalskrzypczyk.home_screen.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.InfoDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.presentation.store.components.StoreModeCard
import com.rafalskrzypczyk.home_screen.presentation.store.components.StorePremiumCard
import com.rafalskrzypczyk.home_screen.presentation.store.components.StoreProgressHeader
import com.rafalskrzypczyk.home_screen.presentation.store.components.StoreSectionHeader

@Composable
fun StoreScreen(
    state: StoreState,
    onEvent: (StoreUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context as? android.app.Activity }
    val scrollState = rememberScrollState()
    val isScrolled by remember { derivedStateOf { scrollState.value > 0 } }

    LaunchedEffect(Unit) {
        onEvent(StoreUIEvents.GetData)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavTopBar(
                    title = stringResource(R.string.title_store)
                ) { onNavigateBack() }
            }

            if (isScrolled) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            ) {
                when (state.responseState) {
                    is ResponseState.Loading, ResponseState.Idle -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Loading()
                        }
                    }
                    is ResponseState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Error loading store: ${state.responseState.message}")
                        }
                    }
                    is ResponseState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                        ) {
                            Spacer(Modifier.padding(top = Dimens.SMALL_PADDING))

                            StoreProgressHeader(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                unlocked = state.unlockedItemsCount,
                                total = state.totalItemsCount
                            )

                            StorePremiumCard(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                title = state.fullPackageProduct?.name ?: stringResource(R.string.premium_package_title),
                                description = state.fullPackageProduct?.description ?: stringResource(R.string.premium_package_desc),
                                price = state.fullPackageProduct?.price,
                                isUnlocked = state.isPremium,
                                isPending = state.isPremiumPending,
                                isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_FULL_PACKAGE,
                                bestValueLabel = stringResource(R.string.store_badge_best_value),
                                savingsText = state.savingsText,
                                onBuyClick = {
                                    activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_FULL_PACKAGE)) }
                                }
                            )

                            StoreSectionHeader(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                title = stringResource(R.string.store_section_modes),
                                subtitle = stringResource(R.string.store_section_modes_desc),
                                count = 2,
                                icon = Icons.Default.Layers
                            )

                            StoreModeCard(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                title = state.translationModeProduct?.name ?: stringResource(com.rafalskrzypczyk.core.R.string.feature_translation_title),
                                description = state.translationModeProduct?.description ?: stringResource(com.rafalskrzypczyk.core.R.string.mode_translation_desc),
                                icon = ModeInfoProvider.getIcon(QuizMode.TranslationMode),
                                iconTint = ModeInfoProvider.getColor(QuizMode.TranslationMode),
                                price = state.translationModeProduct?.price,
                                isUnlocked = state.isTranslationModeUnlocked,
                                isPending = state.isTranslationModePending,
                                isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_TRANSLATION_MODE,
                                onBuyClick = {
                                    activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_TRANSLATION_MODE)) }
                                }
                            )

                            StoreModeCard(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                title = state.swipeModeProduct?.name ?: stringResource(com.rafalskrzypczyk.core.R.string.feature_swipe_title),
                                description = state.swipeModeProduct?.description ?: stringResource(com.rafalskrzypczyk.core.R.string.mode_swipe_desc),
                                icon = ModeInfoProvider.getIcon(QuizMode.SwipeMode),
                                iconTint = ModeInfoProvider.getColor(QuizMode.SwipeMode),
                                price = state.swipeModeProduct?.price,
                                isUnlocked = state.isSwipeModeUnlocked,
                                isPending = state.isSwipeModePending,
                                isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_SWIPE_MODE,
                                onBuyClick = {
                                    activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_SWIPE_MODE)) }
                                }
                            )

                            if (state.categoryPacks.isNotEmpty()) {
                                StoreSectionHeader(
                                    modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                    title = stringResource(R.string.store_section_packs),
                                    subtitle = stringResource(R.string.store_section_packs_desc),
                                    count = state.categoryPacks.size,
                                    icon = Icons.AutoMirrored.Filled.LibraryBooks
                                )

                                state.categoryPacks.forEach { pack ->
                                    StoreModeCard(
                                        modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                        title = pack.title,
                                        description = pack.description,
                                        icon = Icons.AutoMirrored.Filled.List,
                                        iconTint = MQYellow,
                                        price = pack.price,
                                        isUnlocked = pack.isUnlocked,
                                        isPending = pack.isPending,
                                        isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == pack.sku,
                                        questionCount = pack.questionCount,
                                        onBuyClick = {
                                            activity?.let { onEvent(StoreUIEvents.BuyProduct(it, pack.sku)) }
                                        }
                                    )
                                }
                            }

                            StoreSectionHeader(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                title = stringResource(R.string.store_section_convenience),
                                subtitle = stringResource(R.string.store_section_convenience_desc),
                                count = 1,
                                icon = Icons.Default.AutoAwesome
                            )

                            StoreModeCard(
                                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                                title = state.adFreeProduct?.name ?: stringResource(R.string.ad_free_title),
                                description = state.adFreeProduct?.description ?: stringResource(R.string.ad_free_desc),
                                icon = Icons.Rounded.Block,
                                iconTint = MQRed,
                                price = state.adFreeProduct?.price,
                                isUnlocked = state.isAdFreeUnlocked,
                                isPending = state.isAdFreePending,
                                isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_AD_FREE,
                                onBuyClick = {
                                    activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_AD_FREE)) }
                                }
                            )

                            Spacer(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)))
                        }
                    }
                }
            }
        }

        if (state.purchaseError != null) {
            InfoDialog(
                title = stringResource(id = com.rafalskrzypczyk.core.R.string.desc_error),
                message = state.purchaseError,
                icon = Icons.Default.PriorityHigh,
                headerColor = MQRed,
                onDismiss = { onEvent(StoreUIEvents.ConsumeError) }
            )
        }
    }
}

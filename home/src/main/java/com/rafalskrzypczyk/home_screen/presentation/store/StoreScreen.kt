package com.rafalskrzypczyk.home_screen.presentation.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.getCategoryBillingId
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.InfoDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.MQBlue
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.presentation.store.components.StoreModeCard
import com.rafalskrzypczyk.home_screen.presentation.store.components.StorePremiumCard

@Composable
fun StoreScreen(
    state: StoreState,
    onEvent: (StoreUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    var topBarHeight by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val activity = remember(context) { context as? android.app.Activity }

    LaunchedEffect(Unit) {
        onEvent(StoreUIEvents.GetData)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))

        Box(contentAlignment = Alignment.TopCenter) {
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
                        modifier = modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                    ) {
                        androidx.compose.foundation.layout.Spacer(
                            Modifier.height(
                                with(LocalDensity.current) {
                                    topBarHeight.toDp() + WindowInsets.safeDrawing.getTop(this).toDp()
                                }
                            )
                        )

                        // Pełny Pakiet Premium (Złota Karta)
                        StorePremiumCard(
                            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                            price = state.fullPackagePrice,
                            isUnlocked = state.isPremium,
                            isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_FULL_PACKAGE,
                            onBuyClick = {
                                activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_FULL_PACKAGE)) }
                            }
                        )

                        // Tłumaczenia
                        StoreModeCard(
                            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                            title = stringResource(R.string.feature_translation_title),
                            description = stringResource(R.string.mode_translation_desc),
                            icon = Icons.Rounded.Translate,
                            iconTint = MQBlue,
                            price = state.translationModePrice,
                            isUnlocked = state.isTranslationModeUnlocked,
                            isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_TRANSLATION_MODE,
                            onBuyClick = {
                                activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_TRANSLATION_MODE)) }
                            }
                        )

                        // Swipe Mode
                        StoreModeCard(
                            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                            title = stringResource(R.string.feature_swipe_title),
                            description = stringResource(R.string.mode_swipe_desc),
                            icon = Icons.Rounded.DynamicFeed,
                            iconTint = MQGreen,
                            price = state.swipeModePrice,
                            isUnlocked = state.isSwipeModeUnlocked,
                            isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_SWIPE_MODE,
                            onBuyClick = {
                                activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_SWIPE_MODE)) }
                            }
                        )

                        val categoryIdStr = getCategoryBillingId(98226763913716L)
                        StoreModeCard(
                            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                            title = stringResource(R.string.store_category_title),
                            description = stringResource(R.string.store_category_desc),
                            icon = Icons.AutoMirrored.Filled.List,
                            iconTint = MQYellow,
                            price = state.categoryPrice,
                            isUnlocked = state.isCategoryUnlocked,
                            isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == categoryIdStr,
                            onBuyClick = {
                                activity?.let { onEvent(StoreUIEvents.BuyProduct(it, categoryIdStr)) }
                            }
                        )

                        // Ad-Free (Brak reklam na samym dole)
                        StoreModeCard(
                            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
                            title = stringResource(R.string.ad_free_title),
                            description = stringResource(R.string.ad_free_desc),
                            icon = Icons.Rounded.Block,
                            iconTint = MQRed,
                            price = state.adFreePrice,
                            isUnlocked = state.isAdFreeUnlocked,
                            isPurchasing = state.isPurchasing && state.pendingPurchaseModeId == BillingIds.ID_AD_FREE,
                            onBuyClick = {
                                activity?.let { onEvent(StoreUIEvents.BuyProduct(it, BillingIds.ID_AD_FREE)) }
                            }
                        )

                        androidx.compose.foundation.layout.Spacer(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)))
                    }
                }
            }

            NavTopBar(
                modifier = Modifier.onGloballyPositioned {
                    topBarHeight = it.size.height
                },
                title = stringResource(R.string.title_store)
            ) { onNavigateBack() }
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

package com.rafalskrzypczyk.main_mode.presentation.categories_screen.components

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ExitButton
import com.rafalskrzypczyk.core.composables.FeatureItem
import com.rafalskrzypczyk.core.composables.PremiumBadge
import com.rafalskrzypczyk.core.composables.PurchaseActionSection
import com.rafalskrzypczyk.core.composables.PurchaseFeature
import com.rafalskrzypczyk.core.composables.QuestionsBadge
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.R
import kotlinx.coroutines.launch

/**
 * Panel zakupu pojedynczej kategorii — bottom sheet w języku wizualnym paywalli trybów,
 * ale z zakresem informacji skrojonym pod treść kategorii (liczba pytań, opis, co otrzymujesz).
 * Współdzieli maszynę stanów CTA przez [PurchaseActionSection].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPurchaseBottomSheet(
    categoryTitle: String,
    categoryDescription: String,
    questionCount: Int,
    price: String?,
    isUnlocked: Boolean,
    isPending: Boolean,
    isPurchasing: Boolean,
    purchaseError: String?,
    activity: Activity?,
    shouldDismiss: Boolean,
    onDismiss: () -> Unit,
    onBuyClick: (Activity) -> Unit,
    onStartClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val closeSheet: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    LaunchedEffect(shouldDismiss) {
        if (shouldDismiss) closeSheet()
    }

    BackHandler(enabled = sheetState.isVisible) {
        closeSheet()
    }

    ModalBottomSheet(
        onDismissRequest = closeSheet,
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        shape = RoundedCornerShape(0.dp),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxSize(),
        sheetGesturesEnabled = false
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            CategoryPurchaseContent(
                categoryTitle = categoryTitle,
                categoryDescription = categoryDescription,
                questionCount = questionCount,
                price = price,
                isUnlocked = isUnlocked,
                isPending = isPending,
                isPurchasing = isPurchasing,
                purchaseError = purchaseError,
                onDismiss = closeSheet,
                onBuyClick = { activity?.let { onBuyClick(it) } },
                onStartClick = onStartClick
            )
        }
    }
}

@Composable
private fun CategoryPurchaseContent(
    categoryTitle: String,
    categoryDescription: String,
    questionCount: Int,
    price: String?,
    isUnlocked: Boolean,
    isPending: Boolean,
    isPurchasing: Boolean,
    purchaseError: String?,
    onDismiss: () -> Unit,
    onBuyClick: () -> Unit,
    onStartClick: () -> Unit
) {
    val themeColor = ModeInfoProvider.getColor(QuizMode.MainMode)
    val icon = ModeInfoProvider.getIcon(QuizMode.MainMode)

    // Zapamiętane z wejścia — dzięki temu po zakupie gra animacja sukcesu zamiast od razu badge'a „Posiadane".
    val isAlreadyUnlockedOnEntry = remember { isUnlocked }

    val scrollState = rememberScrollState()
    val isScrolled by remember { derivedStateOf { scrollState.value > 0 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            ExitButton(onClose = onDismiss)
            TextTitle(
                text = categoryTitle,
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier = Modifier
                    .align(Alignment.Center)
                    // Odstęp = szerokość ExitButton, by wyśrodkowany tytuł nie nachodził na X.
                    .padding(horizontal = Dimens.IMAGE_SIZE_SMALL)
            )
        }

        if (isScrolled) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(themeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBadge()
                QuestionsBadge(questionCount = questionCount, themeColor = themeColor)
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextPrimary(
                text = categoryDescription,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING)
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextPrimary(
                text = stringResource(R.string.category_purchase_benefits_title),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            categoryBenefits().forEach { feature ->
                FeatureItem(feature, themeColor)
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            }

            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
        }

        PurchaseActionSection(
            themeColor = themeColor,
            price = price,
            purchaseError = purchaseError,
            isPurchasing = isPurchasing,
            isUnlocked = isUnlocked,
            isPending = isPending,
            isAlreadyUnlockedOnEntry = isAlreadyUnlockedOnEntry,
            onBuyClick = onBuyClick,
            onStartClick = onStartClick,
            buyButtonModifier = Modifier.testTag(TestTags.PURCHASE_DIALOG_BUY_BUTTON)
        )
    }
}

@Composable
private fun categoryBenefits(): List<PurchaseFeature> = listOf(
    PurchaseFeature(
        title = stringResource(R.string.category_feature_access_title),
        description = stringResource(R.string.category_feature_access_desc),
        icon = Icons.Outlined.Quiz
    ),
    PurchaseFeature(
        title = stringResource(R.string.category_feature_lifetime_title),
        description = stringResource(R.string.category_feature_lifetime_desc),
        icon = Icons.Default.AllInclusive
    )
)

@Preview(name = "Category purchase")
@Preview(name = "Category purchase dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CategoryPurchaseContentPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            CategoryPurchaseContent(
                categoryTitle = "Kardiologia",
                categoryDescription = "Pytania z zakresu chorób i stanów nagłych układu krążenia.",
                questionCount = 142,
                price = "19,99 zł",
                isUnlocked = false,
                isPending = false,
                isPurchasing = false,
                purchaseError = null,
                onDismiss = {},
                onBuyClick = {},
                onStartClick = {}
            )
        }
    }
}

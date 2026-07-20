package com.rafalskrzypczyk.core.composables

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.core.utils.QuizMode

object TrialFinishedPanelDefaults {
    val ICON_CONTAINER_SIZE = Dimens.ICON_BACKGROUND_SIZE_LARGE
    val ICON_SIZE = 40.dp
}

/**
 * Panel pokazywany po wyczerpaniu darmowych pytań w trialu trybu płatnego.
 * Współdzielony przez tryby swipe i tłumaczeń — moduły dostarczają tylko tryb i teksty.
 *
 * Ten sam język wizualny co panele zakupowe na Home ([BasePurchaseBottomSheet]), ale bez listy
 * korzyści: użytkownik właśnie przeszedł tryb, więc tłumaczenie mu na czym on polega byłoby
 * powtórką. Zostaje sama oferta — ile pytań odblokowuje zakup, za ile.
 *
 * Ekran quizu ukrywa na czas panelu swój górny pasek, więc panel rysuje własny nagłówek
 * i sam obsługuje insety: [WindowInsets.statusBars] tutaj, pasek nawigacji w [PurchaseActionSection].
 *
 * @param modeTitle nazwa trybu do paska ("gdzie jestem")
 * @param title nagłówek treści ("co się stało")
 */
@Composable
fun TrialFinishedPanel(
    mode: QuizMode,
    modeTitle: String,
    title: String,
    description: String,
    questionCount: Int,
    price: String?,
    onBuyClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPurchasing: Boolean = false,
    isPending: Boolean = false,
    purchaseError: String? = null,
    buyButtonModifier: Modifier = Modifier
) {
    val themeColor = ModeInfoProvider.getColor(mode)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            ExitButton(onClose = onExitClick)
            TextTitle(
                text = modeTitle,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Treść mieści się na ekranie, ale scroll zostaje na wypadek małych ekranów
        // i dużych skal czcionki. Wyśrodkowanie działa dopóki treść jest krótsza niż viewport.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(TrialFinishedPanelDefaults.ICON_CONTAINER_SIZE)
                    .background(color = themeColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ModeInfoProvider.getIcon(mode),
                    contentDescription = null,
                    modifier = Modifier.size(TrialFinishedPanelDefaults.ICON_SIZE),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            TextTitle(text = title, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBadge()
                QuestionsBadge(questionCount = questionCount, themeColor = themeColor)
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            TextPrimary(
                text = description,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING)
            )
        }

        PurchaseActionSection(
            themeColor = themeColor,
            price = price,
            purchaseError = purchaseError,
            isPurchasing = isPurchasing,
            isUnlocked = false,
            isPending = isPending,
            isAlreadyUnlockedOnEntry = false,
            onBuyClick = onBuyClick,
            onStartClick = {},
            onTryClick = onExitClick,
            secondaryLabel = stringResource(R.string.btn_exit_quiz),
            buyButtonModifier = buyButtonModifier
        )
    }
}

@Preview
@Composable
private fun TrialFinishedPanelSwipePreview() {
    PreviewContainer {
        TrialFinishedPanel(
            mode = QuizMode.SwipeMode,
            modeTitle = stringResource(R.string.title_swipe_mode),
            title = stringResource(R.string.trial_finished_title),
            description = stringResource(R.string.trial_finished_desc),
            questionCount = 240,
            price = "19,99 zł",
            onBuyClick = {},
            onExitClick = {}
        )
    }
}

@Preview
@Composable
private fun TrialFinishedPanelTranslationPreview() {
    PreviewContainer {
        TrialFinishedPanel(
            mode = QuizMode.TranslationMode,
            modeTitle = stringResource(R.string.title_translation_mode),
            title = stringResource(R.string.trial_finished_translation_title),
            description = stringResource(R.string.trial_finished_translation_desc),
            questionCount = 180,
            price = "14,99 zł",
            onBuyClick = {},
            onExitClick = {}
        )
    }
}

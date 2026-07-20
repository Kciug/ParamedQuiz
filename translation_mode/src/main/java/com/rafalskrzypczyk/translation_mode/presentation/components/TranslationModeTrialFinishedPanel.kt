package com.rafalskrzypczyk.translation_mode.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TrialFinishedPanel
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.utils.QuizMode

@Composable
fun TranslationModeTrialFinishedPanel(
    onBuyClick: () -> Unit,
    onExitClick: () -> Unit,
    totalQuestions: Int,
    price: String?,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    isPending: Boolean = false,
    error: String? = null
) {
    TrialFinishedPanel(
        mode = QuizMode.TranslationMode,
        modeTitle = stringResource(R.string.title_translation_mode),
        title = stringResource(R.string.trial_finished_translation_title),
        description = stringResource(R.string.trial_finished_translation_desc),
        questionCount = totalQuestions,
        price = price,
        onBuyClick = onBuyClick,
        onExitClick = onExitClick,
        modifier = modifier,
        isPurchasing = loading,
        isPending = isPending,
        purchaseError = error,
        buyButtonModifier = Modifier.testTag(TestTags.TRANSLATION_TRIAL_BUY_BUTTON)
    )
}

@Preview
@Composable
private fun TranslationModeTrialFinishedPanelPreview() {
    PreviewContainer {
        TranslationModeTrialFinishedPanel(
            onBuyClick = {},
            onExitClick = {},
            totalQuestions = 180,
            price = "14,99 zł"
        )
    }
}

package com.rafalskrzypczyk.swipe_mode.presentation.components

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
fun SwipeModeTrialFinishedPanel(
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
        mode = QuizMode.SwipeMode,
        modeTitle = stringResource(R.string.title_swipe_mode),
        title = stringResource(R.string.trial_finished_title),
        description = stringResource(R.string.trial_finished_desc),
        questionCount = totalQuestions,
        price = price,
        onBuyClick = onBuyClick,
        onExitClick = onExitClick,
        modifier = modifier,
        isPurchasing = loading,
        isPending = isPending,
        purchaseError = error,
        buyButtonModifier = Modifier.testTag(TestTags.SWIPE_TRIAL_BUY_BUTTON)
    )
}

@Preview
@Composable
private fun SwipeModeTrialFinishedPanelPreview() {
    PreviewContainer {
        SwipeModeTrialFinishedPanel(
            onBuyClick = {},
            onExitClick = {},
            totalQuestions = 240,
            price = "19,99 zł"
        )
    }
}

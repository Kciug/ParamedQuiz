package com.rafalskrzypczyk.core.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.utils.QuizMode

/**
 * Treść sprzedażowa trybów płatnych: opis i lista korzyści.
 * Jedno źródło prawdy dla paneli zakupowych na Home ([BasePurchaseBottomSheet])
 * i paneli po wyczerpaniu triala ([TrialFinishedPanel]).
 *
 * Tryby bez osobnego panelu zakupowego zwracają pustą treść.
 */
@Composable
fun modeDescription(mode: QuizMode): String = when (mode) {
    QuizMode.SwipeMode -> stringResource(R.string.mode_swipe_desc)
    QuizMode.TranslationMode -> stringResource(R.string.mode_translation_desc)
    else -> ""
}

@Composable
fun modeFeatures(mode: QuizMode): List<PurchaseFeature> = when (mode) {
    QuizMode.SwipeMode -> listOf(
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

    QuizMode.TranslationMode -> listOf(
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

    else -> emptyList()
}

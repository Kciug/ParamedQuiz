package com.rafalskrzypczyk.swipe_mode.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextScore
import com.rafalskrzypczyk.core.composables.TextTitle

@Composable
fun SwipeModeTrialFinishedPanel(
    onBuyClick: () -> Unit,
    onExitClick: () -> Unit,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.DEFAULT_PADDING),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = Dimens.ELEVATION,
        shadowElevation = Dimens.ELEVATION
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.DEFAULT_PADDING)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Swipe,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextTitle(
                text = stringResource(R.string.trial_finished_title),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            TextPrimary(
                text = stringResource(R.string.trial_finished_desc),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                TrialStat(
                    modifier = Modifier.weight(1f),
                    value = totalQuestions.toString(),
                    label = stringResource(R.string.stat_questions),
                    icon = Icons.AutoMirrored.Filled.LibraryBooks
                )
                TrialStat(
                    modifier = Modifier.weight(1f),
                    value = "~5",
                    label = stringResource(R.string.stat_time),
                    icon = Icons.Default.Timer
                )
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            ButtonPrimary(
                title = stringResource(R.string.btn_buy_mode),
                onClick = onBuyClick
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            ButtonSecondary(
                title = stringResource(R.string.btn_exit_quiz),
                onClick = onExitClick
            )
        }
    }
}

@Composable
private fun TrialStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        TextScore(text = value, color = MaterialTheme.colorScheme.primary)
        TextCaption(text = label, textAlign = TextAlign.Center)
    }
}

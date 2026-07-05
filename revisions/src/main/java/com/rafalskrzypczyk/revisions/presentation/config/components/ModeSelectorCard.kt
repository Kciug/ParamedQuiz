package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.core.utils.ModeInfoProvider
import com.rafalskrzypczyk.revisions.R

@Composable
fun ModeSelectorCard(
    mode: QuizMode,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.DEFAULT_PADDING)
            ) {
                val icon = ModeInfoProvider.getIcon(mode)
                val color = ModeInfoProvider.getColor(mode)

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (enabled) color else color.copy(alpha = 0.38f),
                            shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextHeadline(
                        text = when (mode) {
                            QuizMode.MainMode -> stringResource(R.string.revisions_mode_main)
                            QuizMode.CemMode -> stringResource(R.string.revisions_mode_cem)
                            QuizMode.TranslationMode -> stringResource(R.string.revisions_mode_translation)
                            else -> ""
                        },
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        textAlign = TextAlign.Start
                    )

                    TextPrimary(
                        text = if (enabled) {
                            when (mode) {
                                QuizMode.MainMode -> "Utrwalaj pytania ze standardowych kategorii"
                                QuizMode.CemMode -> "Powtarzaj pytania z oficjalnej bazy CEM"
                                QuizMode.TranslationMode -> "Przećwicz słownictwo z trybu tłumaczeń"
                                else -> ""
                            }
                        } else {
                            stringResource(R.string.revisions_mode_not_enough_answers)
                        },
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        textAlign = TextAlign.Start
                    )
                }
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = Dimens.ELEMENTS_SPACING, end = Dimens.ELEMENTS_SPACING)
                        .size(24.dp)
                )
            }
        }
    }
}

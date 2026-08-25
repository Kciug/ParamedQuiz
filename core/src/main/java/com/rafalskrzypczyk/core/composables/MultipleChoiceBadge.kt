package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

private object MultipleChoiceBadgeDefaults {
    val HorizontalPadding = 6.dp
    val VerticalPadding = 1.dp
    val Spacing = 4.dp
    val IconSize = 10.dp
    val Shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
    const val BackgroundAlpha = 0.15f
}

/**
 * Oznaczenie pytania, na które poprawnych odpowiedzi jest więcej niż jedna.
 *
 * Renderowane nad listą odpowiedzi (patrz `QuizGameContent`), a nie w nagłówku — informacja dotyczy
 * sposobu odpowiadania, więc siedzi przy odpowiedziach i jest identyczna we wszystkich trybach
 * opartych o wspólną zawartość quizu.
 */
@Composable
fun MultipleChoiceBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag(TestTags.QUIZ_MULTIPLE_CHOICE_BADGE),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = MultipleChoiceBadgeDefaults.BackgroundAlpha),
        shape = MultipleChoiceBadgeDefaults.Shape
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MultipleChoiceBadgeDefaults.HorizontalPadding,
                vertical = MultipleChoiceBadgeDefaults.VerticalPadding
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MultipleChoiceBadgeDefaults.Spacing)
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(MultipleChoiceBadgeDefaults.IconSize)
            )
            TextCaption(
                text = stringResource(R.string.badge_multiple_choice),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MultipleChoiceBadgePreview() {
    ParamedQuizTheme {
        Surface {
            MultipleChoiceBadge(modifier = Modifier.padding(Dimens.DEFAULT_PADDING))
        }
    }
}

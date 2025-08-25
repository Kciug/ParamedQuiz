package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.main_mode.R

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: CategoryUIM,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        onClick = onClick
    ) {
        Box {
            Column(
                modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextHeadline(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT))
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = Dimens.SMALL_PADDING)
                            .wrapContentHeight(Alignment.CenterVertically),
                        text = category.title,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Row {
                        TextPrimary(text = category.questionCount)
                        Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
                        Icon(
                            imageVector = Icons.Outlined.Style,
                            contentDescription = stringResource(R.string.desc_questions_number),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                TextPrimary(text = category.description)
                if(category.progress > 0) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { category.progress },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        drawStopIndicator = {}
                    )
                }
            }

            if(!category.unlocked) CategoryLockedOverlay(modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
fun CategoryLockedOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = stringResource(R.string.desc_category_locked),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(Dimens.IMAGE_SIZE_SMALL)
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun CategoryCardPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CategoryCard(
                category = CategoryUIM(
                    id = 1,
                    title = "Kategoria",
                    description = "Opis kategorii",
                    questionCount = "10",
                    unlocked = true,
                    progress = 0.7f
                )
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun CategoryCardLockedPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CategoryCard(
                category = CategoryUIM(
                    id = 1,
                    title = "Kategoria",
                    description = "Opis kategorii",
                    questionCount = "10",
                    unlocked = false,
                    progress = 0f
                )
            )
        }
    }
}
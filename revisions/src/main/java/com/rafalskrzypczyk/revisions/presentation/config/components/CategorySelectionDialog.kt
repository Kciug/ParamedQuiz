package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory

@Composable
fun CategorySelectionDialog(
    categories: List<RevisionCategory>,
    selectedCategory: RevisionCategory?,
    onCategorySelected: (RevisionCategory) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = Dimens.LARGE_PADDING)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.DEFAULT_PADDING)
            ) {
                TextHeadline(
                    text = stringResource(R.string.revisions_select_pool)
                )
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(categories) { category ->
                        val isSelected = category.id == selectedCategory?.id
                        val containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        }

                        Card(
                            onClick = {
                                if (category.isEligible) {
                                    onCategorySelected(category)
                                    onDismiss()
                                }
                            },
                            enabled = category.isEligible,
                            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(Dimens.DEFAULT_PADDING)
                            ) {
                                TextPrimary(
                                    text = category.title,
                                    fontWeight = FontWeight.Bold,
                                    color = if (category.isEligible) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                                if (category.isEligible) {
                                    TextCaption(text = "Dostępne pytania: ${category.totalQuestionsCount}")
                                } else {
                                    TextCaption(
                                        text = stringResource(
                                            R.string.revisions_category_not_enough_answers,
                                            category.answeredQuestionsCount
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                ButtonSecondary(
                    title = stringResource(android.R.string.cancel),
                    onClick = onDismiss
                )
            }
        }
    }
}

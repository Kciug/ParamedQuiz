package com.rafalskrzypczyk.core.composables.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.OwnedBadge
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

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
        onClick = rememberDebouncedClick(onClick = onClick)
    ) {
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
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = Dimens.SMALL_PADDING)
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = category.title,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (category.description.isNotEmpty()) {
                TextPrimary(text = category.description)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (category.subcategoriesCount > 0) {
                    TextPrimary(text = category.subcategoriesCount.toString())
                    Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = stringResource(R.string.desc_subcategories_number),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
                }
                TextPrimary(text = category.questionCount)
                Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
                Icon(
                    imageVector = Icons.Outlined.Style,
                    contentDescription = stringResource(R.string.desc_questions_number),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                if (category.isPremium) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = MQYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
                    
                    val badgeText = if (category.unlocked) {
                        stringResource(R.string.badge_owned)
                    } else {
                        stringResource(R.string.badge_to_buy)
                    }
                    
                    OwnedBadge(
                        text = badgeText,
                        icon = if (category.unlocked) Icons.Default.CheckCircle else Icons.Default.ShoppingCart,
                        backgroundColor = (if (category.unlocked) MQGreen else MaterialTheme.colorScheme.secondary).copy(alpha = 0.2f),
                        contentColor = if (category.unlocked) MQGreen else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (category.progress > 0) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = category.progress,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

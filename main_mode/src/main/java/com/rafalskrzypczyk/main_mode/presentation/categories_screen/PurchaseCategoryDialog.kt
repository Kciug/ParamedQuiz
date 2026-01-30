package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.BasePurchaseDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.main_mode.R

@Composable
fun PurchaseCategoryDialog(
    category: CategoryUIM,
    price: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BasePurchaseDialog(
        price = price,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        TextTitle(
            text = category.title,
            textAlign = TextAlign.Center
        )

        val questionCountInt = category.questionCount.toIntOrNull() ?: 0
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Quiz,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            TextCaption(
                text = pluralStringResource(
                    id = R.plurals.questions_count_plurals,
                    count = questionCountInt,
                    questionCountInt
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextPrimary(
            text = category.description,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.BaseCustomDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.main_mode.R

@Composable
fun PurchaseCategoryDialog(
    category: CategoryUIM,
    price: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = Icons.Default.ShoppingCart,
        title = stringResource(R.string.purchase_dialog_title),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextHeadline(text = category.title, color = MaterialTheme.colorScheme.primary)
                TextPrimary(text = category.description)
                if (price != null) {
                    TextPrimary(text = stringResource(R.string.price_label, price))
                } else {
                    TextPrimary(text = stringResource(R.string.price_loading))
                }
            }
        },
        buttons = {
            TextButton(onClick = onDismiss) {
                TextPrimary(text = stringResource(R.string.btn_cancel), color = MaterialTheme.colorScheme.primary)
            }
            TextButton(
                onClick = onConfirm,
                enabled = price != null
            ) {
                TextPrimary(text = stringResource(R.string.btn_buy), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

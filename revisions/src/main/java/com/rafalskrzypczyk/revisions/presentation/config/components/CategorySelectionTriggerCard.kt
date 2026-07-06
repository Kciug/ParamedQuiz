package com.rafalskrzypczyk.revisions.presentation.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory

@Composable
fun CategorySelectionTriggerCard(
    category: RevisionCategory?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TextCaption(text = "Wybrana kategoria")
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                TextHeadline(
                    text = category?.title ?: "Wybierz kategorię..."
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null
            )
        }
    }
}

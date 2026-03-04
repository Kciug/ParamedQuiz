package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.home.R

@Composable
fun HomeScreenAddonsMenu(
    modifier: Modifier = Modifier,
    addons: List<Addon>
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextHeadline(
            text = stringResource(R.string.title_addons),
            modifier = Modifier.padding(start = Dimens.DEFAULT_PADDING, top = Dimens.DEFAULT_PADDING)
        )
        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.DEFAULT_PADDING),
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            items(addons, key = { it.title }) { addon ->
                AddonButton(
                    addon = addon
                )
            }
        }
    }
}

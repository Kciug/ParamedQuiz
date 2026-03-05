package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.firestore.domain.models.NewsBannerDTO

@Composable
fun HomeNewsBanner(
    banner: NewsBannerDTO,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        banner.imageUrl?.let { url ->
            NewsBannerImage(imageUrl = url)
        }

        if (banner.title != null || banner.body != null) {
            NewsBannerTextContent(
                title = banner.title,
                body = banner.body
            )
        }
    }
}

@Composable
private fun NewsBannerImage(
    imageUrl: String
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RADIUS_SMALL)),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun NewsBannerTextContent(
    title: String?,
    body: String?
) {
    Column(
        modifier = Modifier
            .padding(Dimens.DEFAULT_PADDING)
    ) {
        title?.let {
            TextTitle(
                text = it,
                modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING_SMALL)
            )
        }
        body?.let {
            TextPrimary(text = it)
        }
    }
}

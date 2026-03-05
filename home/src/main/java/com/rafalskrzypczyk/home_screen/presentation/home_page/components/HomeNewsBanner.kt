package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.firestore.domain.models.NewsBannerDTO

@Composable
fun HomeNewsBanner(
    banner: NewsBannerDTO,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
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

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.SMALL_PADDING)
                .size(24.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
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

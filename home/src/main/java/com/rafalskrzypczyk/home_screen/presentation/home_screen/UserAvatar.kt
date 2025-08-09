package com.rafalskrzypczyk.home_screen.presentation.home_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.Dimens

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    isUserLoggedIn: Boolean,
    userAvatar: String? = null,
    onNavigateToUserPanel: () -> Unit
) {
    if (isUserLoggedIn) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.avatar_default),
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(Dimens.IMAGE_SIZE_SMALL)
                .clip(CircleShape)
                .aspectRatio(1f)
                .background(Color.Transparent)
                .clickable(onClick = onNavigateToUserPanel)
        )
    } else {
        Icon(
            modifier = modifier
                .size(Dimens.IMAGE_SIZE_SMALL)
                .clip(CircleShape)
                .clickable(onClick = onNavigateToUserPanel),
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = stringResource(com.rafalskrzypczyk.core.R.string.desc_user_avatar)
        )
    }
}
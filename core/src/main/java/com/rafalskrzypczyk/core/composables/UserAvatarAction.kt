package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

@Composable
fun UserAvatarAction(
    modifier: Modifier = Modifier,
    isUserLoggedIn: Boolean,
    @Suppress("unused") userAvatar: String? = null,
    onNavigateToUserPanel: () -> Unit
) {
    Box(
        modifier = modifier
            .size(Dimens.IMAGE_SIZE_SMALL)
            .clip(CircleShape)
            .background(Color.Transparent)
            .clickable(onClick = rememberDebouncedClick(onClick = onNavigateToUserPanel)),
        contentAlignment = Alignment.Center
    ) {
        if (isUserLoggedIn) {
            Image(
                painter = painterResource(R.drawable.avatar_default),
                contentDescription = stringResource(R.string.desc_user_avatar),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(Dimens.IMAGE_SIZE_SMALL)
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .background(Color.Transparent)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = stringResource(R.string.desc_user_avatar)
            )
        }

    }
}
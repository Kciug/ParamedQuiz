package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun UserProfileIconAction(
    userImage: Int? = null,
    onNavigateToUserProfile: () -> Unit
) {
    IconButton(
        onClick = { onNavigateToUserProfile() }
    ) {
        UserIcon(userImage = userImage)
    }
}

@Composable
fun UserIcon(modifier: Modifier = Modifier, userImage: Int?) {
    if(userImage != null) {
        Image(
            painterResource(userImage),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(CircleShape)
                .background(Color.Black)
        )
    } else {
        Icon(
            modifier = modifier,
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = "avatar"
        )
    }
}
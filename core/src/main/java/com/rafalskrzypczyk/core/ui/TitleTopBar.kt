package com.rafalskrzypczyk.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopBar(onNavigateToUserProfile: () -> Unit, userImageId: Int?) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Text("MediQuiz")
        },
        actions = {
            IconButton(
                onClick = { onNavigateToUserProfile() }
            ) {
                UserIcon(userImage = userImageId)
            }
        }
    )
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
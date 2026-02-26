package com.rafalskrzypczyk.core.composables

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun Loading(
    modifier: Modifier = Modifier,
    baseIconSize: androidx.compose.ui.unit.Dp = 50.dp
) {
    val infiniteAnimation = rememberInfiniteTransition(label = "infinite")
    val iconSize by infiniteAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 500
                1.3f at 100
                1f at 300
                1f at 500
            },
            repeatMode = RepeatMode.Restart
        ), label = "Loading"
    )

    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            Modifier.size(baseIconSize * iconSize),
            tint = androidx.compose.material3.LocalContentColor.current
        )
    }
}

@Preview
@Composable
fun LoadingScreenPreview () {
    ParamedQuizTheme {
        Surface {
            Loading()
        }
    }
}
package com.rafalskrzypczyk.core.composables.top_bars

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.BackButton
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ExitButton
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun NavTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigateBack: () -> Unit,
) {
    var lastNonNullTitle by remember { mutableStateOf("") }
    if (title != null) {
        lastNonNullTitle = title
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = title != null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            TextHeadline(lastNonNullTitle)
        }
        Row (
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(onNavigateBack = onNavigateBack)
            actions()
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun NavTopBarWithActionPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavTopBar(
                actions = { ExitButton { } },
            ) { }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun NavTopBarWithTitlePreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavTopBar(
                title = "Test",
            ) { }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun NavTopBarFullPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavTopBar(
                title = "Test",
                actions = { ExitButton { } },
            ) { }
        }
    }
}

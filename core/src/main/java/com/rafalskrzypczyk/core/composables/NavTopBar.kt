package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun NavTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        title?.let {
            TextHeadline(title)
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

package com.rafalskrzypczyk.revisions.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar

@Composable
fun RevisionsConfigScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            NavTopBar(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_revisions_mode),
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            TextTitle(text = stringResource(com.rafalskrzypczyk.core.R.string.title_revisions_mode))
        }
    }
}

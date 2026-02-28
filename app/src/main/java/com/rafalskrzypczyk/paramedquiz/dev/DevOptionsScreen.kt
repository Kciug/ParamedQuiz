package com.rafalskrzypczyk.paramedquiz.dev

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOptionsScreen(
    onEvent: (DevOptionsUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dev Options") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier
                .padding(15.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.ResetOnboarding) }) {
                Text("Reset Onboarding")
            }
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.ResetModularOnboarding) }) {
                Text("Reset Modular Onboarding")
            }
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.ClearTermsAcceptance) }) {
                Text("Reset Terms of Service")
            }
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.ResetRatingStats) }) {
                Text("Reset Rating Stats")
            }
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.TriggerRatingPrompt) }) {
                Text("Trigger Rating Prompt")
            }
        }
    }
}

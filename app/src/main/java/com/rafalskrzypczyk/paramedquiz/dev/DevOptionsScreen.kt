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
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rafalskrzypczyk.notifications.NotificationPermission

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
        val context = LocalContext.current

        // Po decyzji o uprawnieniu i tak próbujemy wysłać — Notifier sam pominie wysyłkę,
        // jeśli powiadomienia są zablokowane.
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { onEvent.invoke(DevOptionsUIEvents.SendTestNotification) }

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
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.ResetNews) }) {
                Text("Reset News")
            }
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.ResetPurchases) }) {
                Text("Reset Purchases")
            }
            Button(onClick = {
                val needsRequest = NotificationPermission.requiresRuntimePermission() &&
                    ContextCompat.checkSelfPermission(
                        context,
                        NotificationPermission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

                if (needsRequest) {
                    notificationPermissionLauncher.launch(NotificationPermission.POST_NOTIFICATIONS)
                } else {
                    onEvent.invoke(DevOptionsUIEvents.SendTestNotification)
                }
            }) {
                Text("Send Test Notification")
            }
            Button(onClick = { onEvent.invoke(DevOptionsUIEvents.TriggerNotificationConsent) }) {
                Text("Trigger Notification Consent")
            }
        }
    }
}

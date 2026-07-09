package com.rafalskrzypczyk.paramedquiz.dev

import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.SettingsCategoryCard
import com.rafalskrzypczyk.core.composables.SettingsCategoryHeader
import com.rafalskrzypczyk.core.composables.SettingsInfoPanel
import com.rafalskrzypczyk.core.composables.SettingsItemRow
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.notifications.NotificationPermission

@Composable
fun DevOptionsScreen(
    onEvent: (DevOptionsUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Po decyzji o uprawnieniu i tak próbujemy wysłać — Notifier sam pominie wysyłkę,
    // jeśli powiadomienia są zablokowane.
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { onEvent.invoke(DevOptionsUIEvents.SendTestNotification) }

    val onSendTestNotification: () -> Unit = {
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
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NavTopBar(title = "Opcje deweloperskie") { onNavigateBack() }
        }
    ) { innerPadding ->
        // Bottom nav bar + dodatkowy oddech, zeby ostatnia karta nie byla ucieta
        val contentInsets = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Bottom)
            .add(WindowInsets(bottom = Dimens.LARGE_PADDING))
            .asPaddingValues()

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = contentInsets
            ) {
                // === Onboarding i zgody ===
                item { SettingsCategoryHeader("Onboarding i zgody") }
                item {
                    DevCategoryCard {
                        DevActionRow(
                            title = "Reset onboardingu",
                            icon = Icons.Outlined.RestartAlt,
                            info = "Efekt po ponownym uruchomieniu aplikacji.",
                            onClick = { onEvent(DevOptionsUIEvents.ResetOnboarding) }
                        )
                        DevActionRow(
                            title = "Reset onboardingu modułowego",
                            icon = Icons.Outlined.RestartAlt,
                            info = "Efekt po restarcie / ponownym wejściu w moduł.",
                            onClick = { onEvent(DevOptionsUIEvents.ResetModularOnboarding) }
                        )
                        DevActionRow(
                            title = "Reset akceptacji regulaminu",
                            icon = Icons.Outlined.RestartAlt,
                            info = "Okno regulaminu pojawi się przy następnym starcie.",
                            onClick = { onEvent(DevOptionsUIEvents.ClearTermsAcceptance) }
                        )
                    }
                }

                // === Oceny ===
                item { SettingsCategoryHeader("Oceny") }
                item {
                    DevCategoryCard {
                        DevActionRow(
                            title = "Reset statystyk oceny",
                            icon = Icons.Outlined.Star,
                            onClick = { onEvent(DevOptionsUIEvents.ResetRatingStats) }
                        )
                        DevActionRow(
                            title = "Wymuś prośbę o ocenę",
                            icon = Icons.Outlined.Star,
                            info = "Używa Google Play In-App Review — Google może NIE pokazać okna " +
                                "za każdym razem (limity). Wiarygodnie działa tylko na buildzie z Play " +
                                "(staging/internal), nie z Android Studio.",
                            onClick = { onEvent(DevOptionsUIEvents.TriggerRatingPrompt) }
                        )
                    }
                }

                // === Powiadomienia ===
                item { SettingsCategoryHeader("Powiadomienia") }
                item {
                    DevCategoryCard {
                        DevActionRow(
                            title = "Wyślij testowe powiadomienie",
                            icon = Icons.Outlined.Notifications,
                            info = "Wymaga zgody na powiadomienia (Android 13+). Jeśli zablokowane " +
                                "w systemie — nic się nie pokaże.",
                            onClick = onSendTestNotification
                        )
                        DevActionRow(
                            title = "Wymuś prośbę o zgodę na powiadomienia",
                            icon = Icons.Outlined.Notifications,
                            info = "Prompt pojawia się po ukończeniu min. 1 quizu; resetuje stan zgody.",
                            onClick = { onEvent(DevOptionsUIEvents.TriggerNotificationConsent) }
                        )
                        DevActionRow(
                            title = "Uruchom przypomnienie teraz",
                            icon = Icons.Outlined.Notifications,
                            info = "Wymaga włączonych powiadomień.",
                            onClick = { onEvent(DevOptionsUIEvents.TriggerReminderNow) }
                        )
                    }
                }

                // === Symulacje ===
                item { SettingsCategoryHeader("Symulacje") }
                item {
                    DevCategoryCard {
                        DevActionRow(
                            title = "Symuluj streak w toku (wczoraj)",
                            icon = Icons.Outlined.Science,
                            info = "Widoczne na pasku głównym po odświeżeniu ekranu.",
                            onClick = { onEvent(DevOptionsUIEvents.SimStreakPending) }
                        )
                        DevActionRow(
                            title = "Symuluj brak aktywności 7 dni",
                            icon = Icons.Outlined.Science,
                            info = "Ustawia streak sprzed 7 dni (ścieżka winback).",
                            onClick = { onEvent(DevOptionsUIEvents.SimInactive7) }
                        )
                        DevActionRow(
                            title = "Symuluj brak aktywności 14 dni",
                            icon = Icons.Outlined.Science,
                            info = "Ustawia streak sprzed 14 dni (ścieżka winback).",
                            onClick = { onEvent(DevOptionsUIEvents.SimInactive14) }
                        )
                        DevActionRow(
                            title = "Symuluj słabe pytania",
                            icon = Icons.Outlined.Science,
                            info = "Dodaje sztuczne słabe pytania i ustawia przypomnienie o powtórce.",
                            onClick = { onEvent(DevOptionsUIEvents.SimWeakQuestions) }
                        )
                    }
                }

                // === Dane / konfiguracja ===
                item { SettingsCategoryHeader("Dane / konfiguracja") }
                item {
                    DevCategoryCard {
                        DevActionRow(
                            title = "Reset przeczytanych newsów",
                            icon = Icons.Outlined.CloudSync,
                            onClick = { onEvent(DevOptionsUIEvents.ResetNews) }
                        )
                        DevActionRow(
                            title = "Reset zakupów",
                            icon = Icons.Outlined.CloudSync,
                            info = "Konsumuje aktywne zakupy (tylko consumable). Wymaga aktywnego " +
                                "zakupu; konto musi być testerem licencyjnym.",
                            onClick = { onEvent(DevOptionsUIEvents.ResetPurchases) }
                        )
                        DevActionRow(
                            title = "Wymuś odświeżenie konfiguracji",
                            icon = Icons.Outlined.CloudSync,
                            info = "Pobiera teksty powiadomień z Firestore — wymaga internetu.",
                            onClick = { onEvent(DevOptionsUIEvents.ForceConfigRefresh) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Karta kategorii z dodatkowym dolnym oddechem — bez tego panel informacyjny na samym dole
 * kategorii przyklejal sie do zaokraglonej krawedzi karty.
 */
@Composable
private fun DevCategoryCard(content: @Composable ColumnScope.() -> Unit) {
    SettingsCategoryCard {
        content()
        Spacer(modifier = Modifier.height(Dimens.SMALL_PADDING))
    }
}

@Composable
private fun ColumnScope.DevActionRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    info: String? = null
) {
    SettingsItemRow(
        title = title,
        icon = icon,
        showChevron = false,
        onClick = onClick
    )
    if (info != null) {
        SettingsInfoPanel(text = info)
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun DevOptionsScreenPreview() {
    PreviewContainer {
        DevOptionsScreen(onEvent = {}, onNavigateBack = {})
    }
}

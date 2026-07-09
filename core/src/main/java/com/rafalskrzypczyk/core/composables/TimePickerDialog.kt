package com.rafalskrzypczyk.core.composables

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R

/**
 * Reużywalny dialog wyboru godziny oparty na [BaseCustomDialog] + Material3 [TimePicker].
 * Format 12/24h dopasowany do ustawień urządzenia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )

    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = null,
        title = title,
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(state = timeState)
            }
        },
        buttons = {
            TextButton(onClick = onDismiss) {
                TextPrimary(
                    text = stringResource(R.string.btn_cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            TextButton(onClick = { onConfirm(timeState.hour, timeState.minute) }) {
                TextPrimary(
                    text = stringResource(R.string.btn_confirm_OK),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

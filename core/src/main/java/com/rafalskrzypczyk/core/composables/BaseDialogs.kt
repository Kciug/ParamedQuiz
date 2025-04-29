package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun ErrorDialog(
    errorMessage: String,
    onInteraction: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Outlined.Info, contentDescription = "Alert")
        },
        title = {
            Text(text = stringResource(id = R.string.title_error_dialog))
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = errorMessage,
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = onInteraction,
        confirmButton = {
            TextButton(onClick = onInteraction) {
                Text(text = "OK")
            }
        }
    )
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ErrorDialogPreview() {
    ParamedQuizTheme {
        Surface {
            ErrorDialog("Placeholder") { }
        }
    }
}

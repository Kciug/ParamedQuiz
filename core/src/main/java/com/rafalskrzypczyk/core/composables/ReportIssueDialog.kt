package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

@Composable
fun ReportIssueDialog(
    questionText: String,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    val description = remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(Icons.Outlined.BugReport, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.report_issue_title))
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.report_issue_question_preview),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                TextFieldPrimary(
                    textValue = description.value,
                    onValueChange = { description.value = it },
                    hint = stringResource(R.string.report_issue_description_hint)
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = rememberDebouncedClick {
                    if (description.value.isNotBlank()) {
                        onSend(description.value)
                    }
                },
                enabled = description.value.isNotBlank()
            ) {
                Text(text = stringResource(R.string.report_issue_send))
            }
        },
        dismissButton = {
            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                Text(text = stringResource(R.string.report_issue_cancel))
            }
        }
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReportIssueDialogPreview() {
    PreviewContainer {
        ReportIssueDialog(
            questionText = "Przykładowe pytanie z błędem?",
            onDismiss = {},
            onSend = {}
        )
    }
}
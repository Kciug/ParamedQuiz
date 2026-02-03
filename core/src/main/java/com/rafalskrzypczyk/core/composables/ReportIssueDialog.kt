package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material3.MaterialTheme
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

    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = Icons.Rounded.OutlinedFlag,
        title = stringResource(R.string.report_issue_title),
        content = {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.DEFAULT_PADDING)
                ) {
                    TextCaption(
                        text = stringResource(R.string.report_issue_question_preview),
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextPrimary(
                        text = questionText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))

                TextFieldMultiLine(
                    textValue = description.value,
                    onValueChange = { description.value = it },
                    hint = stringResource(R.string.report_issue_description_hint),
                    minLines = 5,
                    maxLines = 8
                )
            }
        },
        buttons = {
            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                TextPrimary(
                    text = stringResource(R.string.report_issue_cancel),
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            val isEnabled = description.value.isNotBlank()
            val sendColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            
            TextButton(
                onClick = rememberDebouncedClick {
                    if (isEnabled) {
                        onSend(description.value)
                    }
                },
                enabled = isEnabled
            ) {
                TextPrimary(
                    text = stringResource(R.string.report_issue_send),
                    color = sendColor
                )
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
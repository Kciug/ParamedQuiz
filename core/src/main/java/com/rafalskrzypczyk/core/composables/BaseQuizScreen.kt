package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R

@Composable
fun BaseQuizScreen(
    title: String,
    score: Int,
    showQuestionNumber: Boolean = true,
    currentQuestionIndex: Int = 0,
    onNavigateBack: () -> Unit,
    onReportIssue: () -> Unit,
    quizContent: @Composable (PaddingValues) -> Unit
) {
    var showBackConfirmation by remember { mutableStateOf(false) }
    val onBackAction = { showBackConfirmation = true }

    BackHandler {
        onBackAction()
    }

    Scaffold(
        topBar = {
            Column {
                NavTopBar(
                    title = title,
                    actions = { ReportAction(onAction = onReportIssue) }
                ) { onBackAction() }
                BaseQuizPanel(
                    score = score,
                    showQuestionNumber = showQuestionNumber,
                    currentQuestionIndex = currentQuestionIndex
                )
            }
        }
    ) { innerPadding ->
        quizContent(innerPadding)
    }

    if(showBackConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_title_confirm_exit_quiz),
            onConfirm = {
                onNavigateBack()
                showBackConfirmation = false
            },
            onDismiss = { showBackConfirmation = false }
        )
    }
}

@Composable
fun BaseQuizPanel(
    score: Int,
    showQuestionNumber: Boolean,
    currentQuestionIndex: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.DEFAULT_PADDING + Dimens.SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if(showQuestionNumber) Arrangement.SpaceBetween else Arrangement.Center
    ) {
        UserPointsLabel(
            modifier = Modifier,
            value = score
        )
        if(showQuestionNumber) {
            TextHeadline(
                modifier = Modifier,
                text = currentQuestionIndex.toString(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                textAlign = TextAlign.End

            )
        }
    }
}

@Composable
fun ReportAction(
    modifier: Modifier = Modifier,
    onAction: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.Rounded.OutlinedFlag,
        description = "Settings"
    ) { onAction() }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BaseQuizScreenPreview() {
    PreviewContainer {
        BaseQuizScreen(
            title = "Test",
            score = 2137,
            currentQuestionIndex = 15,
            showQuestionNumber = true,
            onNavigateBack = {},
            onReportIssue = {}
        ) {}
    }
}
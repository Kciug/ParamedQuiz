package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.top_bars.QuizTopBar

@Composable
fun BaseQuizScreen(
    title: String,
    quizTopPanel: @Composable () -> Unit = {},
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
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            QuizTopBar(
                titlePanel = { BaseQuizTitlePanel(title, currentQuestionIndex) },
                quizPanel = { quizTopPanel() },
                actions = { ReportAction { onReportIssue() } }
            ) { onNavigateBack() }
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
fun BaseQuizTitlePanel(
    title: String,
    currentQuestionIndex: Int
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if(isLandscape) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextHeadline(title)
            TextPrimary(
                text = stringResource(R.string.base_quiz_question_number, currentQuestionIndex),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    } else {
        Column {
            TextHeadline(title)
            TextPrimary(
                text = stringResource(R.string.base_quiz_question_number, currentQuestionIndex),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
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
            currentQuestionIndex = 15,
            onNavigateBack = {},
            onReportIssue = {}
        ) {}
    }
}
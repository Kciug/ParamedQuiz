package com.rafalskrzypczyk.home_screen.presentation.home_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rafalskrzypczyk.home.R

@Composable
fun DailyExerciseDoneDialog(
    onInteraction: () -> Unit
) {
    AlertDialog(
        icon = {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                ))
            ) { }
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.Green
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_daily_exercise_already_done))
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.text_daily_exercise_already_done),
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = onInteraction,
        confirmButton = {
            TextButton(onClick = onInteraction) {
                Text(text = stringResource(com.rafalskrzypczyk.core.R.string.btn_confirm_OK))
            }
        }
    )
}
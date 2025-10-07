package com.rafalskrzypczyk.home_screen.presentation.home_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.home.R
import kotlinx.coroutines.delay

@Composable
fun DailyExerciseDoneDialog(
    onInteraction: () -> Unit
) {
    val iconVisible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        iconVisible.value = true
    }

    AlertDialog(
        icon = {
            Box(modifier = Modifier.size(24.dp)){
                AnimatedVisibility(
                    visible = iconVisible.value,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy
                        ),
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                    )
                }
            }
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

@Composable
fun RevisionsUnavailableDialog(
    onInteraction: () -> Unit
) {
    val iconVisible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        iconVisible.value = true
    }

    AlertDialog(
        icon = {
            Box(modifier = Modifier.size(24.dp)){
                AnimatedVisibility(
                    visible = iconVisible.value,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy
                        ),
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Upcoming,
                        contentDescription = null,
                        tint = MQYellow,
                    )
                }
            }
        },
        title = {
            Text(text = stringResource(id = R.string.title_revisions_unavailable))
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.text_revisions_unavailable),
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
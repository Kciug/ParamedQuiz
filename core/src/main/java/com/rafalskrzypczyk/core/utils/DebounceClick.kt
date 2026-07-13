package com.rafalskrzypczyk.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.LocalFeedbackManager

const val DEBOUNCE_TIME_MS = 500L

@Composable
fun rememberDebouncedClick(
    debounceTime: Long = DEBOUNCE_TIME_MS,
    onClick: () -> Unit
): () -> Unit {
    val currentOnClick = rememberUpdatedState(onClick)
    val feedbackManager = LocalFeedbackManager.current

    return remember(debounceTime) {
        object : () -> Unit {
            var lastClickTime = 0L

            override fun invoke() {
                val now = System.currentTimeMillis()
                if (now - lastClickTime > debounceTime) {
                    lastClickTime = now
                    feedbackManager.perform(FeedbackEvent.CLICK)
                    currentOnClick.value.invoke()
                }
            }
        }
    }
}

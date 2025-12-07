package com.rafalskrzypczyk.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

const val DEBOUNCE_TIME_MS = 500L

@Composable
fun rememberDebouncedClick(
    debounceTime: Long = DEBOUNCE_TIME_MS,
    onClick: () -> Unit
): () -> Unit {
    val currentOnClick = rememberUpdatedState(onClick)

    return remember(debounceTime) {
        object : () -> Unit {
            var lastClickTime = 0L
            
            override fun invoke() {
                val now = System.currentTimeMillis()
                if (now - lastClickTime > debounceTime) {
                    lastClickTime = now
                    currentOnClick.value.invoke()
                }
            }
        }
    }
}
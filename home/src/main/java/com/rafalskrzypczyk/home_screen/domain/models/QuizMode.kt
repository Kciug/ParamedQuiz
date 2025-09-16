package com.rafalskrzypczyk.home_screen.domain.models

enum class QuizMode {
    MainMode,
    SwipeMode
}

fun QuizMode.next(): QuizMode {
    val entries = QuizMode.entries
    val nextIndex = (this.ordinal + 1) % entries.size
    return entries[nextIndex]
}

fun QuizMode.previous(): QuizMode {
    val entries = QuizMode.entries
    val prevIndex = if (this.ordinal - 1 < 0) entries.size - 1 else this.ordinal - 1
    return entries[prevIndex]
}
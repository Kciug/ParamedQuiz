package com.rafalskrzypczyk.core.feedback

enum class FeedbackEvent {
    CLICK,
    ANSWER_CORRECT,
    ANSWER_WRONG,
    QUIZ_COMPLETED,
    NEW_RECORD,
    ERROR
}

interface FeedbackManager {
    fun perform(event: FeedbackEvent)
}

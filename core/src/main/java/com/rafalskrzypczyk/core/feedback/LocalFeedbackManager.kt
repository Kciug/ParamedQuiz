package com.rafalskrzypczyk.core.feedback

import androidx.compose.runtime.staticCompositionLocalOf

object NoOpFeedbackManager : FeedbackManager {
    override fun perform(event: FeedbackEvent) = Unit
}

val LocalFeedbackManager = staticCompositionLocalOf<FeedbackManager> { NoOpFeedbackManager }

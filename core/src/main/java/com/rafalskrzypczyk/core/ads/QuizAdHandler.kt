package com.rafalskrzypczyk.core.ads

import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizAdHandler @Inject constructor(
    private val premiumStatusProvider: PremiumStatusProvider
) {
    private var isAdsFree = false
    private var isFinishingQuiz = false
    private var lastAdAnsweredCount = 0

    fun initialize(scope: CoroutineScope) {
        lastAdAnsweredCount = 0
        isFinishingQuiz = false
        scope.launch {
            premiumStatusProvider.isAdsFree.collectLatest { free ->
                isAdsFree = free
            }
        }
    }

    fun shouldShowAd(answeredCount: Int, isQuizFinished: Boolean): Boolean {
        if (isAdsFree) return false

        val questionsSinceLastAd = answeredCount - lastAdAnsweredCount

        if (isQuizFinished) {
            if (questionsSinceLastAd >= AdConfig.EXIT_AD_THRESHOLD) {
                isFinishingQuiz = true
                lastAdAnsweredCount = answeredCount
                return true
            }
            return false
        }

        // Mid-quiz ad: every AD_FREQUENCY questions
        if (answeredCount > 0 && answeredCount % AdConfig.AD_FREQUENCY == 0 && answeredCount > lastAdAnsweredCount) {
            isFinishingQuiz = false
            lastAdAnsweredCount = answeredCount
            return true
        }

        return false
    }

    fun handleAdDismissed(onContinue: () -> Unit, onFinish: () -> Unit) {
        if (isFinishingQuiz) {
            onFinish()
        } else {
            onContinue()
        }
    }
}

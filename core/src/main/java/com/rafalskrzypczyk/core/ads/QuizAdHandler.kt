package com.rafalskrzypczyk.core.ads

import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizAdHandler @Inject constructor(
    private val premiumStatusProvider: PremiumStatusProvider,
    private val gameplayConfig: GameplayConfigProvider
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

    fun shouldShowAd(
        answeredCount: Int,
        isQuizFinished: Boolean,
        ignoreThreshold: Boolean = false
    ): Boolean {
        if (isAdsFree) return false

        val questionsSinceLastAd = answeredCount - lastAdAnsweredCount

        if (isQuizFinished) {
            if (ignoreThreshold || questionsSinceLastAd >= gameplayConfig.exitAdThreshold()) {
                isFinishingQuiz = true
                return true
            }
            return false
        }

        if (answeredCount > 0 && answeredCount % gameplayConfig.adFrequency() == 0 && answeredCount > lastAdAnsweredCount) {
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

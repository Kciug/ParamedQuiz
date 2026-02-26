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
    private val adFrequency = 20

    fun initialize(scope: CoroutineScope) {
        scope.launch {
            premiumStatusProvider.isAdsFree.collectLatest { free ->
                isAdsFree = free
            }
        }
    }

    fun shouldShowAd(answeredCount: Int, isQuizFinished: Boolean): Boolean {
        if (isAdsFree) return false

        if (isQuizFinished) {
            isFinishingQuiz = true
            return true
        }

        if (answeredCount > 0 && answeredCount % adFrequency == 0) {
            isFinishingQuiz = false
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

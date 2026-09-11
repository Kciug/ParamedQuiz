package com.rafalskrzypczyk.core.ads

import android.app.Activity

interface AdManager {
    fun initialize(activity: Activity)
    fun showInterstitial(activity: Activity, onAdShown: () -> Unit, onAdDismissed: () -> Unit)

    /**
     * Liczba odpowiedzi od poprzedniej reklamy, zapamiętywana na potrzeby `ad_shown`.
     *
     * Wołane przez [QuizAdHandler] w chwili decyzji o pokazaniu reklamy: sam AdManager nie widzi
     * przebiegu quizu, a przekazywanie tej liczby przez composable oznaczałoby przewleczenie jej
     * przez stan i sześć ekranów.
     */
    fun onInterstitialTriggered(answersSinceLastAd: Int)

    /** Debug/testowe: czyści stan zgody UMP, żeby formularz pojawił się przy następnym starcie. */
    fun resetConsent()
}

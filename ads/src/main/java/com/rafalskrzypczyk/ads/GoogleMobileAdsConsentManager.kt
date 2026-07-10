package com.rafalskrzypczyk.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

class GoogleMobileAdsConsentManager(context: Context) {
    private val consentInformation: ConsentInformation = UserMessagingPlatform.getConsentInformation(context)

    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: (FormError?) -> Unit
    ) {
        val paramsBuilder = ConsentRequestParameters.Builder()

        // W debugu wymuszamy geografię EOG, żeby formularz zgody pojawiał się na emulatorze /
        // poza EOG (bez tego Google uznaje, że zgoda nie jest wymagana i formularz się nie pokazuje).
        // Emulatory są automatycznie traktowane jako test device — hash niepotrzebny. Dla FIZYCZNEGO
        // urządzenia poza EOG dodaj hashed ID z logcatu przez .addTestDeviceHashedId("...").
        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }

        val params = paramsBuilder.build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    onConsentGatheringCompleteListener(formError)
                }
            },
            { requestConsentError ->
                onConsentGatheringCompleteListener(requestConsentError)
            }
        )
    }

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /** Czyści zapisany stan zgody — po tym następne [gatherConsent] ponownie pokaże formularz. */
    fun reset() {
        consentInformation.reset()
    }
}

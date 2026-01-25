package com.rafalskrzypczyk.ads

import android.app.Activity
import android.content.Context
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
        val params = ConsentRequestParameters.Builder()
            .build()

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
}

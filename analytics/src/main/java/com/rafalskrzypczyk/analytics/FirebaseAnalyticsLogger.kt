package com.rafalskrzypczyk.analytics

import android.os.Bundle
import android.os.Parcelable
import com.google.firebase.analytics.FirebaseAnalytics
import com.rafalskrzypczyk.core.analytics.AnalyticsConsent
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsUserProperty

/**
 * Produkcyjna implementacja [AnalyticsLogger] oparta o Firebase Analytics (GA4).
 *
 * Nie wstrzykuje [com.rafalskrzypczyk.core.error.ErrorLogger] — powstałby cykl
 * `ErrorLogger -> AnalyticsLogger -> ErrorLogger`. Ewentualne błędy dostawcy zostają tutaj.
 */
class FirebaseAnalyticsLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {

    override fun log(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name, event.toBundle())
    }

    override fun setUserProperty(property: AnalyticsUserProperty, value: String) {
        firebaseAnalytics.setUserProperty(property.propertyName, value)
    }

    override fun setConsent(consent: AnalyticsConsent) {
        firebaseAnalytics.setConsent(
            mapOf(
                FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE to consent.analyticsStorage.toStatus(),
                FirebaseAnalytics.ConsentType.AD_STORAGE to consent.adStorage.toStatus(),
                FirebaseAnalytics.ConsentType.AD_USER_DATA to consent.adUserData.toStatus(),
                FirebaseAnalytics.ConsentType.AD_PERSONALIZATION to consent.adPersonalization.toStatus(),
            )
        )
    }

    private fun Boolean.toStatus() =
        if (this) FirebaseAnalytics.ConsentStatus.GRANTED else FirebaseAnalytics.ConsentStatus.DENIED

    private fun AnalyticsEvent.toBundle(): Bundle {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Long -> bundle.putLong(key, value)
                is Int -> bundle.putLong(key, value.toLong())
                is Double -> bundle.putDouble(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        if (this is AnalyticsEvent.PurchaseStandard) bundle.putItems(this)
        return bundle
    }

    /** Standardowy `purchase` w GA4 wymaga tablicy `items` — jedyne pole spoza mapy parametrów. */
    private fun Bundle.putItems(event: AnalyticsEvent.PurchaseStandard) {
        val item = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, event.productId)
            putDouble(FirebaseAnalytics.Param.PRICE, event.value)
            putString(FirebaseAnalytics.Param.CURRENCY, event.currency)
            putLong(FirebaseAnalytics.Param.QUANTITY, 1L)
        }
        putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf<Parcelable>(item))
    }
}

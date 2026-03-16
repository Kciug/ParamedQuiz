package com.rafalskrzypczyk.billing.data

import com.android.billingclient.api.BillingClient
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.utils.ResourceProvider
import javax.inject.Inject

class BillingError @Inject constructor(
    private val resourcesProvider: ResourceProvider
) {
    fun localizedError(responseCode: Int): String {
        return when (responseCode) {
            BillingClient.BillingResponseCode.USER_CANCELED -> resourcesProvider.getString(R.string.billing_error_user_cancelled)
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> resourcesProvider.getString(R.string.billing_error_service_unavailable)
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> resourcesProvider.getString(R.string.billing_error_billing_unavailable)
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> resourcesProvider.getString(R.string.billing_error_service_disconnected)
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> resourcesProvider.getString(R.string.billing_error_item_unavailable)
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> resourcesProvider.getString(R.string.billing_error_item_already_owned)
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> resourcesProvider.getString(R.string.billing_error_developer_error)
            BillingClient.BillingResponseCode.ERROR -> resourcesProvider.getString(R.string.billing_error_generic)
            else -> resourcesProvider.getString(R.string.billing_error_generic)
        }
    }
}

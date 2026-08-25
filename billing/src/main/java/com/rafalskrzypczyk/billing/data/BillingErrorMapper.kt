package com.rafalskrzypczyk.billing.data

import com.android.billingclient.api.BillingClient
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.error.report
import javax.inject.Inject

/**
 * Tłumaczy kody odpowiedzi Biblioteki Płatności Google na [AppError].
 * Każde wywołanie [toAppError] i [report] jest jednocześnie punktem logowania.
 */
class BillingErrorMapper @Inject constructor(
    private val errorLogger: ErrorLogger
) {
    fun toAppError(origin: String, responseCode: Int): AppError {
        val error = fromResponseCode(responseCode)
        errorLogger.log(origin, error)
        return error
    }

    fun report(origin: String, error: AppError): AppError = errorLogger.report(origin, error)

    private fun fromResponseCode(responseCode: Int): AppError = when (responseCode) {
        BillingClient.BillingResponseCode.USER_CANCELED -> AppError.Billing.UserCancelled
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> AppError.Billing.ServiceUnavailable
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> AppError.Billing.BillingUnavailable
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> AppError.Billing.ServiceDisconnected
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> AppError.Billing.ItemUnavailable
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> AppError.Billing.ItemAlreadyOwned
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> AppError.Billing.DeveloperError
        else -> AppError.Billing.Unknown(PREFIX + responseCode)
    }

    private companion object {
        const val PREFIX = "BILLING:"
    }
}

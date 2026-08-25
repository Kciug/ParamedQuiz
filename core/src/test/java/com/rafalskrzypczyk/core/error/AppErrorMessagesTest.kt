package com.rafalskrzypczyk.core.error

import org.junit.Assert.assertNotEquals
import org.junit.Test

class AppErrorMessagesTest {

    private val allErrors: List<AppError> = listOf(
        AppError.Auth.InvalidCredentials,
        AppError.Auth.InvalidEmail,
        AppError.Auth.WrongPassword,
        AppError.Auth.WeakPassword,
        AppError.Auth.EmailAlreadyInUse,
        AppError.Auth.OperationNotAllowed,
        AppError.Auth.TooManyRequests,
        AppError.Auth.RecentLoginRequired,
        AppError.Auth.UserNotLoggedIn,
        AppError.Auth.ReauthContextMissing,
        AppError.Auth.SignInIncomplete,
        AppError.Auth.Unknown("AUTH:X"),
        AppError.Google.Cancelled,
        AppError.Google.NoCredentialAvailable,
        AppError.Google.ProviderConfiguration,
        AppError.Google.Interrupted,
        AppError.Google.UnsupportedCredential,
        AppError.Google.MalformedIdToken,
        AppError.Google.Unknown("GIS:X"),
        AppError.Data.PermissionDenied,
        AppError.Data.Unavailable,
        AppError.Data.Aborted,
        AppError.Data.NotFound,
        AppError.Data.DeadlineExceeded,
        AppError.Data.NoData,
        AppError.Data.Unknown("FS:X"),
        AppError.Billing.UserCancelled,
        AppError.Billing.ServiceUnavailable,
        AppError.Billing.BillingUnavailable,
        AppError.Billing.ServiceDisconnected,
        AppError.Billing.ItemUnavailable,
        AppError.Billing.ItemAlreadyOwned,
        AppError.Billing.DeveloperError,
        AppError.Billing.ProductDetailsMissing,
        AppError.Billing.Unknown("BILLING:1"),
        AppError.NoNetwork,
        AppError.Unexpected
    )

    @Test
    fun `every error variant resolves to a real string resource`() {
        allErrors.forEach { error ->
            assertNotEquals("Brak zasobu tekstowego dla $error", 0, error.messageRes())
        }
    }

    @Test
    fun `diagnostic code never leaks into the message resource`() {
        val unknownVariants = listOf(
            AppError.Auth.Unknown("AUTH:ERROR_SECRET") to AppError.Auth.Unknown("AUTH:ERROR_OTHER"),
            AppError.Google.Unknown("GIS:A") to AppError.Google.Unknown("GIS:B"),
            AppError.Data.Unknown("FS:A") to AppError.Data.Unknown("FS:B"),
            AppError.Billing.Unknown("BILLING:1") to AppError.Billing.Unknown("BILLING:2")
        )

        unknownVariants.forEach { (first, second) ->
            assertNotEquals(0, first.messageRes())
            assert(first.messageRes() == second.messageRes())
        }
    }
}

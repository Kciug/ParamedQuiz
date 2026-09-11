package com.rafalskrzypczyk.core.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Kontrakt `error_type` jest append-only, więc nazwy muszą być unikalne i stabilne.
 * Test chroni też przed regresją, w której dwa warianty `Unknown` z różnych przestrzeni
 * zlałyby się w jedną wartość.
 */
class AppErrorAnalyticsTest {

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
        AppError.Auth.ProfileRestoreFailed,
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
        AppError.Unexpected,
    )

    @Test
    fun `every error variant maps to a unique analytics name`() {
        val names = allErrors.map { it.analyticsName() }

        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `analytics names are snake case and fit the GA4 value limit`() {
        allErrors.forEach { error ->
            val name = error.analyticsName()

            assertTrue(name, name.matches(Regex("[a-z0-9_]+")))
            assertTrue(name, name.length <= 100)
        }
    }

    @Test
    fun `billing error code drops the namespace prefix`() {
        assertEquals("item_already_owned", AppError.Billing.ItemAlreadyOwned.analyticsCode())
        assertEquals("product_details_missing", AppError.Billing.ProductDetailsMissing.analyticsCode())
    }

    @Test
    fun `non billing error code keeps its namespace`() {
        assertEquals("no_network", AppError.NoNetwork.analyticsCode())
        assertEquals("auth_wrong_password", AppError.Auth.WrongPassword.analyticsCode())
    }
}

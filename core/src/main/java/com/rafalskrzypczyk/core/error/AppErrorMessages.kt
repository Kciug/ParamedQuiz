package com.rafalskrzypczyk.core.error

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.R

/**
 * Zamienia [AppError] na komunikat dla użytkownika. Jedyne miejsce w aplikacji,
 * w którym błąd domenowy spotyka się z zasobem tekstowym.
 */
@Composable
fun AppError.asMessage(): String = stringResource(id = messageRes())

@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.Auth.InvalidCredentials -> R.string.fb_error_invalid_credentials
    AppError.Auth.InvalidEmail -> R.string.fb_error_invalid_email
    AppError.Auth.WrongPassword -> R.string.fb_error_invalid_password
    AppError.Auth.WeakPassword -> R.string.fb_error_weak_password
    AppError.Auth.EmailAlreadyInUse -> R.string.fb_error_email_already_in_use
    AppError.Auth.OperationNotAllowed -> R.string.fb_error_operation_not_allowed
    AppError.Auth.TooManyRequests -> R.string.fb_error_too_many_requests
    AppError.Auth.RecentLoginRequired -> R.string.error_reauth_required
    AppError.Auth.UserNotLoggedIn -> R.string.error_reauth_required
    AppError.Auth.ReauthContextMissing -> R.string.error_try_later
    AppError.Auth.SignInIncomplete -> R.string.error_try_later
    is AppError.Auth.Unknown -> R.string.error_unknown

    AppError.Google.Cancelled -> R.string.error_try_later
    AppError.Google.NoCredentialAvailable -> R.string.error_google_no_account
    AppError.Google.ProviderConfiguration -> R.string.error_google_signin_failed
    AppError.Google.Interrupted -> R.string.error_google_signin_failed
    AppError.Google.UnsupportedCredential -> R.string.error_google_signin_failed
    AppError.Google.MalformedIdToken -> R.string.error_google_signin_failed
    is AppError.Google.Unknown -> R.string.error_google_signin_failed

    AppError.Data.PermissionDenied -> R.string.fb_error_permission_denied
    AppError.Data.Unavailable -> R.string.fb_error_unavailable
    AppError.Data.Aborted -> R.string.fb_error_aborted
    AppError.Data.NotFound -> R.string.fb_error_not_found
    AppError.Data.DeadlineExceeded -> R.string.fb_error_deadline_exceeded
    AppError.Data.NoData -> R.string.error_no_data
    is AppError.Data.Unknown -> R.string.error_unknown

    AppError.Billing.UserCancelled -> R.string.billing_error_user_cancelled
    AppError.Billing.ServiceUnavailable -> R.string.billing_error_service_unavailable
    AppError.Billing.BillingUnavailable -> R.string.billing_error_billing_unavailable
    AppError.Billing.ServiceDisconnected -> R.string.billing_error_service_disconnected
    AppError.Billing.ItemUnavailable -> R.string.billing_error_item_unavailable
    AppError.Billing.ItemAlreadyOwned -> R.string.billing_error_item_already_owned
    AppError.Billing.DeveloperError -> R.string.billing_error_developer_error
    AppError.Billing.ProductDetailsMissing -> R.string.purchase_error_no_details
    is AppError.Billing.Unknown -> R.string.billing_error_generic

    AppError.NoNetwork -> R.string.error_no_network
    AppError.Unexpected -> R.string.error_unknown
}

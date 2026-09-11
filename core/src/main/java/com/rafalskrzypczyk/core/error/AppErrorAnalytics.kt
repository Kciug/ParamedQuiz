package com.rafalskrzypczyk.core.error

/**
 * Nazwa wariantu [AppError] w formacie parametru analitycznego (`error_type`).
 *
 * Prefiks przestrzeni jest obowiązkowy — bez niego `Unknown` z czterech grup byłoby nieodróżnialne.
 * `when` jest wyczerpujący i bez `else`: nowy wariant [AppError] ma zepsuć kompilację, a nie ciszę
 * w raportach. Wzorzec skopiowany z [messageRes].
 */
fun AppError.analyticsName(): String = when (this) {
    AppError.Auth.InvalidCredentials -> "auth_invalid_credentials"
    AppError.Auth.InvalidEmail -> "auth_invalid_email"
    AppError.Auth.WrongPassword -> "auth_wrong_password"
    AppError.Auth.WeakPassword -> "auth_weak_password"
    AppError.Auth.EmailAlreadyInUse -> "auth_email_already_in_use"
    AppError.Auth.OperationNotAllowed -> "auth_operation_not_allowed"
    AppError.Auth.TooManyRequests -> "auth_too_many_requests"
    AppError.Auth.RecentLoginRequired -> "auth_recent_login_required"
    AppError.Auth.UserNotLoggedIn -> "auth_user_not_logged_in"
    AppError.Auth.ReauthContextMissing -> "auth_reauth_context_missing"
    AppError.Auth.SignInIncomplete -> "auth_sign_in_incomplete"
    AppError.Auth.ProfileRestoreFailed -> "auth_profile_restore_failed"
    is AppError.Auth.Unknown -> "auth_unknown"

    AppError.Google.Cancelled -> "google_cancelled"
    AppError.Google.NoCredentialAvailable -> "google_no_credential_available"
    AppError.Google.ProviderConfiguration -> "google_provider_configuration"
    AppError.Google.Interrupted -> "google_interrupted"
    AppError.Google.UnsupportedCredential -> "google_unsupported_credential"
    AppError.Google.MalformedIdToken -> "google_malformed_id_token"
    is AppError.Google.Unknown -> "google_unknown"

    AppError.Data.PermissionDenied -> "data_permission_denied"
    AppError.Data.Unavailable -> "data_unavailable"
    AppError.Data.Aborted -> "data_aborted"
    AppError.Data.NotFound -> "data_not_found"
    AppError.Data.DeadlineExceeded -> "data_deadline_exceeded"
    AppError.Data.NoData -> "data_no_data"
    is AppError.Data.Unknown -> "data_unknown"

    AppError.Billing.UserCancelled -> "billing_user_cancelled"
    AppError.Billing.ServiceUnavailable -> "billing_service_unavailable"
    AppError.Billing.BillingUnavailable -> "billing_billing_unavailable"
    AppError.Billing.ServiceDisconnected -> "billing_service_disconnected"
    AppError.Billing.ItemUnavailable -> "billing_item_unavailable"
    AppError.Billing.ItemAlreadyOwned -> "billing_item_already_owned"
    AppError.Billing.DeveloperError -> "billing_developer_error"
    AppError.Billing.ProductDetailsMissing -> "billing_product_details_missing"
    is AppError.Billing.Unknown -> "billing_unknown"

    AppError.NoNetwork -> "no_network"
    AppError.Unexpected -> "unexpected"
}

/**
 * Kod błędu bez prefiksu przestrzeni — używany jako `error_code` w lejku zakupowym, gdzie
 * przestrzeń jest już znana z nazwy zdarzenia.
 */
fun AppError.analyticsCode(): String = analyticsName().removePrefix("billing_")

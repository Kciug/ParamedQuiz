package com.rafalskrzypczyk.core.error

/**
 * Domenowa reprezentacja błędu. Nie zawiera tekstu dla użytkownika ani wyjątku.
 * Lokalizacja odbywa się w warstwie UI przez [messageRes], diagnostyka techniczna
 * trafia do [ErrorLogger] w momencie utworzenia instancji.
 *
 * Warianty [Auth.Unknown], [Google.Unknown], [Data.Unknown] i [Billing.Unknown] niosą kod
 * z prefiksem przestrzeni, żeby wpis w logu był jednoznaczny.
 */
sealed interface AppError {

    sealed interface Auth : AppError {
        data object InvalidCredentials : Auth
        data object InvalidEmail : Auth
        data object WrongPassword : Auth
        data object WeakPassword : Auth
        data object EmailAlreadyInUse : Auth
        data object OperationNotAllowed : Auth
        data object TooManyRequests : Auth
        data object RecentLoginRequired : Auth
        data object UserNotLoggedIn : Auth
        data object ReauthContextMissing : Auth
        data object SignInIncomplete : Auth
        data object ProfileRestoreFailed : Auth
        data class Unknown(val code: String) : Auth
    }

    sealed interface Google : AppError {
        data object Cancelled : Google
        data object NoCredentialAvailable : Google
        data object ProviderConfiguration : Google
        data object Interrupted : Google
        data object UnsupportedCredential : Google
        data object MalformedIdToken : Google
        data class Unknown(val code: String) : Google
    }

    sealed interface Data : AppError {
        data object PermissionDenied : Data
        data object Unavailable : Data
        data object Aborted : Data
        data object NotFound : Data
        data object DeadlineExceeded : Data
        data object NoData : Data
        data class Unknown(val code: String) : Data
    }

    sealed interface Billing : AppError {
        data object UserCancelled : Billing
        data object ServiceUnavailable : Billing
        data object BillingUnavailable : Billing
        data object ServiceDisconnected : Billing
        data object ItemUnavailable : Billing
        data object ItemAlreadyOwned : Billing
        data object DeveloperError : Billing
        data object ProductDetailsMissing : Billing
        data class Unknown(val code: String) : Billing
    }

    data object NoNetwork : AppError
    data object Unexpected : AppError
}

package com.rafalskrzypczyk.auth.data

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.error.report
import javax.inject.Inject

/**
 * Tłumaczy wyjątki Firebase Auth i Credential Managera na [AppError].
 *
 * Każde wywołanie [toAppError] i [report] jest jednocześnie punktem logowania.
 * Wyjątkiem jest [AppError.Google.Cancelled], czyli zamknięcie okna wyboru konta
 * przez użytkownika, które nie jest awarią.
 */
class AuthErrorMapper @Inject constructor(
    private val errorLogger: ErrorLogger
) {
    fun toAppError(origin: String, throwable: Throwable): AppError {
        val error = map(throwable)
        if (error != AppError.Google.Cancelled) errorLogger.log(origin, error, throwable)
        return error
    }

    fun report(origin: String, error: AppError): AppError = errorLogger.report(origin, error)

    private fun map(throwable: Throwable): AppError = when (throwable) {
        is FirebaseNetworkException -> AppError.NoNetwork
        is FirebaseAuthException -> fromAuthCode(throwable.errorCode)
        is GoogleIdTokenParsingException -> AppError.Google.MalformedIdToken
        is GetCredentialCancellationException -> AppError.Google.Cancelled
        is NoCredentialException -> AppError.Google.NoCredentialAvailable
        is GetCredentialProviderConfigurationException -> AppError.Google.ProviderConfiguration
        is GetCredentialInterruptedException -> AppError.Google.Interrupted
        is GetCredentialUnsupportedException -> AppError.Google.UnsupportedCredential
        is GetCredentialException -> AppError.Google.Unknown(GOOGLE_PREFIX + throwable.type)
        else -> AppError.Auth.Unknown(AUTH_PREFIX + throwable.javaClass.simpleName)
    }

    private fun fromAuthCode(code: String): AppError = when (code.uppercase()) {
        "ERROR_INVALID_CREDENTIAL" -> AppError.Auth.InvalidCredentials
        "ERROR_USER_NOT_FOUND" -> AppError.Auth.InvalidCredentials
        "ERROR_INVALID_EMAIL" -> AppError.Auth.InvalidEmail
        "ERROR_WRONG_PASSWORD" -> AppError.Auth.WrongPassword
        "ERROR_EMAIL_ALREADY_IN_USE" -> AppError.Auth.EmailAlreadyInUse
        "ERROR_WEAK_PASSWORD" -> AppError.Auth.WeakPassword
        "ERROR_OPERATION_NOT_ALLOWED" -> AppError.Auth.OperationNotAllowed
        "ERROR_TOO_MANY_REQUESTS" -> AppError.Auth.TooManyRequests
        "ERROR_REQUIRES_RECENT_LOGIN" -> AppError.Auth.RecentLoginRequired
        else -> AppError.Auth.Unknown(AUTH_PREFIX + code)
    }

    private companion object {
        const val AUTH_PREFIX = "AUTH:"
        const val GOOGLE_PREFIX = "GIS:"
    }
}

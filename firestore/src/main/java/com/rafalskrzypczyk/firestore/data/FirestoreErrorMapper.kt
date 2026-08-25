package com.rafalskrzypczyk.firestore.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.error.report
import com.rafalskrzypczyk.core.network.NetworkMonitor
import java.io.IOException
import javax.inject.Inject

/**
 * Tłumaczy wyjątki Firestore na [AppError]. Każde wywołanie [toAppError] i [report]
 * jest jednocześnie punktem logowania.
 *
 * Porównanie po stałych [FirebaseFirestoreException.Code] zamiast po nazwie kodu jest
 * odporne na obfuskację i nie miesza przestrzeni kodów Firestore z kodami Firebase Auth.
 */
class FirestoreErrorMapper @Inject constructor(
    private val errorLogger: ErrorLogger,
    private val networkMonitor: NetworkMonitor
) {
    fun toAppError(origin: String, throwable: Throwable): AppError {
        val error = map(throwable)
        errorLogger.log(origin, error, throwable)
        return error
    }

    fun report(origin: String, error: AppError): AppError = errorLogger.report(origin, error)

    private fun map(throwable: Throwable): AppError = when (throwable) {
        is FirebaseFirestoreException -> fromCode(throwable.code)
        is FirebaseNetworkException -> AppError.NoNetwork
        is IOException -> AppError.NoNetwork
        else -> AppError.Data.Unknown(PREFIX + throwable.javaClass.simpleName)
    }

    private fun fromCode(code: FirebaseFirestoreException.Code): AppError = when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.Data.PermissionDenied
        FirebaseFirestoreException.Code.UNAVAILABLE -> whenOnline(AppError.Data.Unavailable)
        FirebaseFirestoreException.Code.ABORTED -> AppError.Data.Aborted
        FirebaseFirestoreException.Code.NOT_FOUND -> AppError.Data.NotFound
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> whenOnline(AppError.Data.DeadlineExceeded)
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> AppError.Auth.UserNotLoggedIn
        else -> AppError.Data.Unknown(PREFIX + code.name)
    }

    private fun whenOnline(error: AppError): AppError =
        if (networkMonitor.isOnline()) error else AppError.NoNetwork

    private companion object {
        const val PREFIX = "FS:"
    }
}

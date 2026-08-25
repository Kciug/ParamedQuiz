package com.rafalskrzypczyk.core.error

/**
 * Jedyny punkt diagnostyki błędów w aplikacji.
 *
 * Obowiązuje reguła "log raz i tylko raz": loguje ten, kto tworzy instancję [AppError].
 * Kto ją przekazuje dalej (use case, ViewModel, ekran), nie loguje.
 */
interface ErrorLogger {
    fun log(origin: String, error: AppError, cause: Throwable? = null)
}

/**
 * Loguje błąd powstały bez wyjątku i zwraca go, żeby dało się go wstawić wprost
 * w miejsce użycia bez rozbijania wyrażenia na dwie instrukcje.
 */
fun ErrorLogger.report(origin: String, error: AppError): AppError {
    log(origin, error)
    return error
}

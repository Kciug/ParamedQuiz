package com.rafalskrzypczyk.core.error

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Domyślna implementacja [ErrorLogger] pisząca do logcat pod tagiem [TAG].
 *
 * Nie konstruować w testach jednostkowych modułów bibliotecznych. Poza modułem `app`
 * `unitTests.isReturnDefaultValues` jest wyłączone i [Log] rzuca wtedy "not mocked".
 */
@Singleton
class LogcatErrorLogger @Inject constructor() : ErrorLogger {
    override fun log(origin: String, error: AppError, cause: Throwable?) {
        val message = "$origin | $error"
        if (cause != null) Log.e(TAG, message, cause) else Log.e(TAG, message)
    }

    private companion object {
        const val TAG = "AppError"
    }
}

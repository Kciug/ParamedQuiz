package com.rafalskrzypczyk.core.utils

import com.rafalskrzypczyk.core.R
import javax.inject.Inject

class FirebaseError @Inject constructor(
    private val resourcesProvider: ResourceProvider
) {
    fun localizedError(code: String): String {
        return when (code.uppercase()) {
            "ERROR_INVALID_CREDENTIAL" -> resourcesProvider.getString(R.string.fb_error_invalid_credentials)
            "ERROR_INVALID_EMAIL" -> resourcesProvider.getString(R.string.fb_error_invalid_email)
            "ERROR_WRONG_PASSWORD" -> resourcesProvider.getString(R.string.fb_error_invalid_password)
            "ERROR_EMAIL_ALREADY_IN_USE" -> resourcesProvider.getString(R.string.fb_error_email_already_in_use)
            "ERROR_WEAK_PASSWORD" -> resourcesProvider.getString(R.string.fb_error_weak_password)
            "ERROR_OPERATION_NOT_ALLOWED" -> resourcesProvider.getString(R.string.fb_error_operation_not_allowed)
            "ERROR_TOO_MANY_REQUESTS" -> resourcesProvider.getString(R.string.fb_error_too_many_requests)
            "PERMISSION_DENIED" -> resourcesProvider.getString(R.string.fb_error_permission_denied)
            "UNAVAILABLE" -> resourcesProvider.getString(R.string.fb_error_unavailable)
            "ABORTED" -> resourcesProvider.getString(R.string.fb_error_aborted)
            "NOT_FOUND" -> resourcesProvider.getString(R.string.fb_error_not_found)
            "DEADLINE_EXCEEDED" -> resourcesProvider.getString(R.string.fb_error_deadline_exceeded)
            else -> resourcesProvider.getString(R.string.error_unknown)
        }
    }
}
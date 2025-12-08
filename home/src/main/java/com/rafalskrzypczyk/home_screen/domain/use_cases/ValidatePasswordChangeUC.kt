package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.home.R
import javax.inject.Inject

class ValidatePasswordChangeUC @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    operator fun invoke(old: String, new: String, repeat: String): String? {
        if (new != repeat) {
            return resourceProvider.getString(R.string.validation_password_not_match)
        }
        if (new.length < 6) {
            return resourceProvider.getString(R.string.validation_password_too_short)
        }
        if (old == new) {
            return resourceProvider.getString(R.string.validation_password_the_same)
        }
        return null
    }
}
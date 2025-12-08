package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.home.R
import javax.inject.Inject

class ValidateUsernameUC @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    operator fun invoke(currentUsername: String, newUsername: String): String? {
        if (currentUsername == newUsername) {
            return resourceProvider.getString(R.string.validation_username_the_same)
        }
        return null
    }
}
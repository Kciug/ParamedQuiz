package com.rafalskrzypczyk.home_screen.domain.use_cases

import javax.inject.Inject

data class UserSettingsUseCases @Inject constructor(
    val getUserData: GetUserDataUC,
    val validateUsername: ValidateUsernameUC,
    val updateUsername: UpdateUsernameUC,
    val validatePasswordChange: ValidatePasswordChangeUC,
    val updatePassword: UpdatePasswordUC,
    val deleteAccount: DeleteAccountUC,
    val deleteAccountForProvider: DeleteAccountForProviderUC,
    val signOut: SignOutUC,
    val deleteProgress: DeleteProgressUC
)
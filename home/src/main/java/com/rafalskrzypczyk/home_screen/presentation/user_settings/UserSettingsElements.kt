package com.rafalskrzypczyk.home_screen.presentation.user_settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PasswordTextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

import androidx.compose.ui.autofill.ContentType

@Composable
fun UserSettingsChangeUserName(
    validationMessage: String? = null,
    onChangeUserName: (String) -> Unit
) {
    var newUserName by remember { mutableStateOf("") }

    Column (
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextFieldPrimary(
            textValue = newUserName,
            onValueChange = { newUserName = it },
            hint = stringResource(R.string.hint_new_username),
            imeAction = ImeAction.Done,
            contentType = ContentType.Username
        )

        if (validationMessage.isNullOrEmpty().not()) {
            TextPrimary(
                text = validationMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        ButtonPrimary(
            title = stringResource(R.string.btn_change_username),
            onClick = { onChangeUserName(newUserName) },
            enabled = newUserName.isNotBlank(),
        )
    }
}

@Composable
fun UserSettingsChangePassword(
    validationMessage: String? = null,
    onChangePassword: (String, String, String) -> Unit,
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordRepeat by remember { mutableStateOf("") }

    Column (
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        PasswordTextFieldPrimary(
            password = oldPassword,
            onPasswordChange = { oldPassword = it },
            hint = stringResource(R.string.hint_old_password),
            imeAction = ImeAction.Next,
            contentType = ContentType.Password
        )

        PasswordTextFieldPrimary(
            password = newPassword,
            onPasswordChange = { newPassword = it },
            hint = stringResource(R.string.hint_new_password),
            imeAction = ImeAction.Next,
            contentType = ContentType.NewPassword
        )

        PasswordTextFieldPrimary(
            password = newPasswordRepeat,
            onPasswordChange = { newPasswordRepeat = it },
            hint = stringResource(R.string.hint_password_repeat),
            imeAction = ImeAction.Done,
            contentType = ContentType.NewPassword
        )

        if (validationMessage.isNullOrEmpty().not()) {
            TextPrimary(
                text = validationMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        ButtonPrimary(
            title = stringResource(R.string.btn_change_password),
            onClick = { onChangePassword(oldPassword, newPassword, newPasswordRepeat) },
            enabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && newPasswordRepeat.isNotBlank()
        )
    }
}

@Composable
fun UserSettingsDeleteAccountWithPassword(
    onDeleteAccount: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    Column (
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextPrimary(
            text = stringResource(R.string.text_delete_account_warning),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        PasswordTextFieldPrimary(
            password = password,
            onPasswordChange = { password = it },
            hint = stringResource(R.string.hint_old_password),
            imeAction = ImeAction.Done,
            contentType = ContentType.Password
        )

        ButtonPrimary(
            title = stringResource(R.string.btn_delete_account),
            onClick = { onDeleteAccount(password) },
            enabled = password.isNotBlank()
        )
    }
}

@Composable
fun UserSettingsDeleteAccountForProvider(
    onDeleteAccount: () -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextPrimary(
            text = stringResource(R.string.text_delete_account_warning),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        ButtonPrimary(
            title = stringResource(R.string.btn_delete_account),
            onClick = { onDeleteAccount() }
        )
    }
}

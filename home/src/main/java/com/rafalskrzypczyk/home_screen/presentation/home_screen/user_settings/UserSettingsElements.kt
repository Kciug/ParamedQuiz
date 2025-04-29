package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PasswordTextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home.R

@Composable
fun UserSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit,
    onClick: () -> Unit,
    isExpanded: Boolean
) {
    Column (
        modifier = modifier
            .animateContentSize()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(Dimens.BUTTON_RADIUS))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(radius = Dp.Infinity)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = stringResource(R.string.desc_expand),
                modifier = Modifier.rotate(if(isExpanded) 90f else 0f)
            )

            Text(text = title)
        }

        if(isExpanded) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.DEFAULT_PADDING)
                    .padding(bottom = Dimens.DEFAULT_PADDING),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)

            ) {
                content()
            }
        }
    }
}

@Composable
fun UserSettingsChangeUserName(
    validationMessage: String? = null,
    onChangeUserName: (String) -> Unit
) {
    var newUserName by remember { mutableStateOf("") }

    TextFieldPrimary(
        textValue = newUserName,
        onValueChange = { newUserName = it },
        hint = stringResource(R.string.hint_new_username),
        imeAction = ImeAction.Done
    )

    if(validationMessage.isNullOrEmpty().not()) {
        Text(
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

@Composable
fun UserSettingsChangePassword(
    validationMessage: String? = null,
    onChangePassword: (String, String, String) -> Unit,
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordRepeat by remember { mutableStateOf("") }

    PasswordTextFieldPrimary(
        password = oldPassword,
        onPasswordChange = { oldPassword = it },
        hint = stringResource(R.string.hint_old_password),
        imeAction = ImeAction.Next,
    )

    PasswordTextFieldPrimary(
        password = newPassword,
        onPasswordChange = { newPassword = it },
        hint = stringResource(R.string.hint_new_password),
        imeAction = ImeAction.Next,
    )

    PasswordTextFieldPrimary(
        password = newPasswordRepeat,
        onPasswordChange = { newPasswordRepeat = it },
        hint = stringResource(R.string.hint_password_repeat),
        imeAction = ImeAction.Done,
    )

    if(validationMessage.isNullOrEmpty().not()) {
        Text(
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

@Composable
fun UserSettingsDeleteAccount(
    onDeleteAccount: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    Text(
        text = stringResource(R.string.text_delete_account_warning),
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )

    PasswordTextFieldPrimary(
        password = password,
        onPasswordChange = { password = it },
        hint = stringResource(R.string.hint_old_password),
        imeAction = ImeAction.Done
    )

    ButtonPrimary(
        title = stringResource(R.string.btn_delete_account),
        onClick = { onDeleteAccount(password) },
        enabled = password.isNotBlank()
    )
}

@Composable
@Preview
private fun UserSettingsItemPreview() {
    var isExpanded by remember { mutableStateOf(false) }

    ParamedQuizTheme {
        Scaffold { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            UserSettingsItem(
                title = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_short),
                content = {
                    Text(text = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_long))
                    ButtonSecondary(
                        title = stringResource(com.rafalskrzypczyk.core.R.string.placeholder_long),
                        onClick = {  }
                    )
                },
                onClick = { isExpanded = !isExpanded },
                isExpanded = isExpanded
            )
        }
    }
}

package com.rafalskrzypczyk.signup.register

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.ExitButton
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PasswordTextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.signup.R

@Composable
fun RegisterScreen(
    viewModel: RegisterVM = hiltViewModel(),
    onUserAuthenticated: () -> Unit,
    onNavigateBack: () -> Unit,
    onExitPressed: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.isSuccess) {
        if (state.value.isSuccess) onUserAuthenticated()
    }

    Scaffold (
        topBar = {
            NavTopBar(
                title = stringResource(R.string.title_register),
                actions = {
                    ExitButton { onExitPressed() }
                }
            ) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)

        if(state.value.isLoading){
            Loading()
        } else {
            RegisterScreenContent(
                modifier = modifier,
                onEvent = { viewModel.onEvent(it) },
            )
        }

        if(state.value.error != null) ErrorDialog(state.value.error!!) {
            viewModel.onEvent(RegisterUIEvents.ClearError)
        }
    }
}

@Composable
fun RegisterScreenContent(
    modifier: Modifier,
    onEvent: (RegisterUIEvents) -> Unit,
){
    var nameText by rememberSaveable { mutableStateOf("") }
    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var passwordConfirmationText by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            Image(
                painter = painterResource(com.rafalskrzypczyk.core.R.drawable.email),
                contentDescription = stringResource(R.string.desc_login),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .size(Dimens.IMAGE_SIZE)
            )

            TextFieldPrimary(
                textValue = nameText,
                onValueChange = { nameText = it },
                hint = stringResource(R.string.hint_user_name),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                contentType = ContentType.Username
            )

            TextFieldPrimary(
                textValue = emailText,
                onValueChange = { emailText = it },
                hint = stringResource(R.string.hint_email),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                contentType = ContentType.EmailAddress
            )

            PasswordTextFieldPrimary(
                password = passwordText,
                onPasswordChange = { passwordText = it },
                hint = stringResource(R.string.hint_password),
                imeAction = ImeAction.Next,
                contentType = ContentType.NewPassword
            )

            PasswordTextFieldPrimary(
                password = passwordConfirmationText,
                onPasswordChange = { passwordConfirmationText = it },
                hint = stringResource(R.string.hint_password_confirmation),
                imeAction = ImeAction.Done,
                contentType = ContentType.NewPassword
            )

            ButtonPrimary(
                title = stringResource(R.string.btn_register),
                onClick = { onEvent(RegisterUIEvents.RegisterWithCredentials(nameText, emailText, passwordText)) },
                enabled = passwordText.isNotBlank() && passwordText == passwordConfirmationText
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RegisterScreenPreview() {
    ParamedQuizTheme {
        Surface {
            RegisterScreenContent (
                modifier = Modifier,
                onEvent = {},
            )
        }
    }
}

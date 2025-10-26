package com.rafalskrzypczyk.signup.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PasswordTextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.signup.GoogleAB
import com.rafalskrzypczyk.signup.R

@Composable
fun LoginScreen(
    viewModel: LoginVM = hiltViewModel(),
    onUserAuthenticated: () -> Unit,
    onNavigateBack: () -> Unit,
    onResetPassword: () -> Unit,
    onRegister: () -> Unit
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state.value.isSuccess) {
        if (state.value.isSuccess) onUserAuthenticated()
    }

    Scaffold (
        topBar = {
            NavTopBar(
                title = stringResource(R.string.title_signup)
            ) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        if(state.value.isLoading){
            Loading()
        } else {
            LoginScreenContent(
                modifier = modifier,
                onEvent = { viewModel.onEvent(it) },
                onResetPassword = onResetPassword,
                onRegister = onRegister
            )
        }

        if(state.value.error != null) ErrorDialog(state.value.error!!) {
            viewModel.onEvent(LoginUIEvents.ClearError)
        }
    }
}

@Composable
fun LoginScreenContent(
    modifier: Modifier,
    onEvent: (LoginUIEvents) -> Unit,
    onResetPassword: () -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current

    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.DEFAULT_PADDING)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.account),
            contentDescription = stringResource(R.string.desc_login),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(Dimens.ELEVATION, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.Transparent)
                .size(Dimens.IMAGE_SIZE)
        )

        TextPrimary(stringResource(R.string.label_login))

        TextFieldPrimary(
            textValue = emailText,
            onValueChange = { emailText = it },
            hint = stringResource(R.string.hint_email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        PasswordTextFieldPrimary(
            password = passwordText,
            onPasswordChange = { passwordText = it },
            hint = stringResource(R.string.hint_password),
            imeAction = ImeAction.Done
        )

        ButtonPrimary(
            title = stringResource(R.string.btn_login),
            onClick = { onEvent(LoginUIEvents.LoginWithCredentials(emailText, passwordText)) },
            enabled = passwordText.isNotBlank() && emailText.isNotBlank()
        )

        HorizontalDivider()

        LoginWithSocialMediaSection(
            loginWithGoogle = { onEvent.invoke(LoginUIEvents.LoginWithGoogle(context)) }
        )

        HorizontalDivider()

        ButtonSecondary(
            title = stringResource(R.string.btn_register),
            onClick = { onRegister() }
        )

        ButtonTertiary(
            title = stringResource(R.string.btn_reset_password),
            onClick = { onResetPassword() }
        )
    }
}

@Composable
fun LoginWithSocialMediaSection(
    modifier: Modifier = Modifier,
    loginWithGoogle: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterHorizontally)
    ) {
        TextPrimary(stringResource(R.string.label_login_with_social_media))
        GoogleAB { loginWithGoogle() }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun LoginScreenPreview() {
    ParamedQuizTheme {
        Surface {
            LoginScreenContent(
                modifier = Modifier,
                onEvent = {},
                onResetPassword = {},
                onRegister = {}
            )
        }
    }
}

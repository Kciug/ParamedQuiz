package com.rafalskrzypczyk.signup.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.signup.R
import com.rafalskrzypczyk.signup.SignupTopBar

@Composable
fun LoginScreen(
    viewModel: LoginVM = hiltViewModel(),
    onUserAuthenticated: () -> Unit,
    onNavigateBack: () -> Unit,
    onResetPassword: () -> Unit,
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state.value.authenticationSuccessfull) onUserAuthenticated()
    }

    Scaffold (
        topBar = {
            SignupTopBar(
                title = stringResource(R.string.title_signup),
                onExit = { onNavigateBack() }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        if(state.value.isLoading){
            Loading()
        } else {
            LoginScreenContent(
                modifier = modifier,
                onEvent = { viewModel.onEvent(it) },
                onResetPassword = onResetPassword
            )
        }

        state.value.error?.let {
            ErrorDialog(
                errorMessage = it,
                onInteraction = {  }
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    modifier: Modifier,
    onEvent: (LoginUIEvents) -> Unit,
    onResetPassword: () -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(com.rafalskrzypczyk.core.R.drawable.account),
            contentDescription = stringResource(R.string.desc_login),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Transparent)
                .size(150.dp)
        )

        TextPrimary(stringResource(R.string.label_login))

        TextFieldPrimary(
            textValue = emailText,
            onValueChange = { emailText = it },
            hint = stringResource(R.string.hint_email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        TextFieldPrimary(
            textValue = passwordText,
            onValueChange = { passwordText = it },
            hint = stringResource(R.string.hint_password),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )

        ButtonTertiary(
            title = stringResource(R.string.btn_reset_password),
            onClick = { onResetPassword() }
        )

        ButtonPrimary(
            title = stringResource(R.string.btn_login),
            onClick = { onEvent(LoginUIEvents.LoginWithCredentials(emailText, passwordText)) },
        )
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
                onResetPassword = {}
            )
        }
    }
}

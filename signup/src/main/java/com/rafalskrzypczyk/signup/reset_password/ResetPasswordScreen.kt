package com.rafalskrzypczyk.signup.reset_password

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.ExitButton
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.signup.R
import com.rafalskrzypczyk.signup.SignupTopBar

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordVM = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onExitPressed: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold (
        topBar = {
            NavTopBar(
                title = stringResource(R.string.title_reset_password),
                actions = {
                    ExitButton { onExitPressed() }
                }
            ) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        if(state.value.isLoading) Loading()
        else ResetPasswordScreenContent(
            modifier = modifier,
            isSuccess = state.value.isSuccess,
            onEvent = { viewModel.onEvent(it) },
            onNavigateBack = onNavigateBack,
        )

        if(state.value.error != null) ErrorDialog(state.value.error!!) {
            viewModel.onEvent(ResetPasswordUIEvents.ClearError)
        }
    }
}

@Composable
fun ResetPasswordScreenContent(
    modifier: Modifier,
    isSuccess: Boolean,
    onEvent: (ResetPasswordUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.DEFAULT_PADDING)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
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

        if(isSuccess) {
            ResetPasswordSuccessContent(
                onNavigateBack = onNavigateBack,
            )
        } else {
            ResetPasswordInput(
                onEvent = onEvent,
            )
        }
    }
}

@Composable
fun ResetPasswordInput(
    onEvent: (ResetPasswordUIEvents) -> Unit,
) {
    var emailText by rememberSaveable { mutableStateOf("") }
    
    Text(stringResource(R.string.label_reset_password))

    TextFieldPrimary(
        textValue = emailText,
        onValueChange = { emailText = it },
        hint = stringResource(R.string.hint_email),
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Done
    )

    ButtonPrimary(
        title = stringResource(R.string.btn_register),
        onClick = { onEvent(ResetPasswordUIEvents.SendResetPasswordEmail(emailText)) },
        enabled = emailText.isNotBlank()
    )
}

@Composable
fun ResetPasswordSuccessContent(
    onNavigateBack: () -> Unit,
) {
    Text(
        text = stringResource(R.string.label_reset_password_success),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING)
    )

    ButtonPrimary(
        title = stringResource(R.string.btn_reset_password_back),
        onClick = onNavigateBack,
    )
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ResetPasswordInputPreview() {
    ParamedQuizTheme {
        Surface {
            ResetPasswordScreenContent(
                modifier = Modifier,
                isSuccess = false,
                onEvent = {},
                onNavigateBack = {},
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ResetPasswordSuccessPreview() {
    ParamedQuizTheme {
        Surface {
            ResetPasswordScreenContent(
                modifier = Modifier,
                isSuccess = true,
                onEvent = {},
                onNavigateBack = {},
            )
        }
    }
}
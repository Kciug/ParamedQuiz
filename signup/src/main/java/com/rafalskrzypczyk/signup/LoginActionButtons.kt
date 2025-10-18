package com.rafalskrzypczyk.signup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.ActionButtonImage
import com.rafalskrzypczyk.core.composables.PreviewContainer

@Composable
fun GoogleAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ActionButtonImage(
        modifier = modifier,
        image = painterResource(R.drawable.ic_google),
        description = stringResource(R.string.desc_login_google)
    ) { onClick() }
}


@Preview
@Composable
private fun GoogleABPreview() {
    PreviewContainer {
        GoogleAB { }
    }
}
package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun TextFieldPrimary(
    modifier: Modifier = Modifier,
    textValue: String = "",
    onValueChange: (String) -> Unit,
    hint: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.None,
) {
    BaseTextField(
        textValue = textValue,
        onValueChange = onValueChange,
        hint = hint,
        keyboardType = keyboardType,
        imeAction = imeAction,
        visualTransformation = VisualTransformation.None,
        trailingIcon = null,
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun TextFieldMultiLine(
    modifier: Modifier = Modifier,
    textValue: String = "",
    onValueChange: (String) -> Unit,
    hint: String = "",
    minLines: Int = 5,
    maxLines: Int = 10,
    imeAction: ImeAction = ImeAction.Default
) {
    BaseTextField(
        textValue = textValue,
        onValueChange = onValueChange,
        hint = hint,
        keyboardType = KeyboardType.Text,
        imeAction = imeAction,
        visualTransformation = VisualTransformation.None,
        trailingIcon = null,
        singleLine = false,
        minLines = minLines,
        maxLines = maxLines,
        modifier = modifier
    )
}

@Suppress("AssignedValueIsNeverRead")
@Composable
fun PasswordTextFieldPrimary(
    modifier: Modifier = Modifier,
    password: String = "",
    onPasswordChange: (String) -> Unit,
    hint: String = "",
    imeAction: ImeAction = ImeAction.None
) {
    var showPassword by remember { mutableStateOf(false) }

    BaseTextField(
        textValue = password,
        onValueChange = onPasswordChange,
        hint = hint,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
            val desc = if (showPassword) "Ukryj hasło" else "Pokaż hasło"

            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(imageVector = icon, contentDescription = desc)
            }
        },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun BaseTextField(
    modifier: Modifier = Modifier,
    textValue: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    visualTransformation: VisualTransformation,
    trailingIcon: (@Composable (() -> Unit))? = null,
    singleLine: Boolean,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    TextField(
        value = textValue,
        onValueChange = onValueChange,
        placeholder = { Text(hint) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TextFieldPrimaryPreview() {
    PreviewContainer {
        TextFieldPrimary(
            onValueChange = {},
            hint = "Email"
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PasswordTextFieldPrimaryPreview() {
    PreviewContainer {
        PasswordTextFieldPrimary(
            onPasswordChange = {},
            hint = "Hasło"
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TextFieldMultiLinePreview() {
    PreviewContainer {
        TextFieldMultiLine(
            onValueChange = {},
            hint = "Opisz problem...",
            textValue = "To jest przykładowy długi tekst, który powinien się zawijać i scrollować, gdy przekroczy ustaloną liczbę linii. Sprawdźmy czy to działa poprawnie."
        )
    }
}
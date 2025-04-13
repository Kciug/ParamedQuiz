package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme


@Composable
fun TextFieldPrimary(
    textValue: String = "",
    onValueChange: (String) -> Unit,
    hint: String = "",
    keyboardType: KeyboardType = KeyboardType.Companion.Text,
    imeAction: ImeAction = ImeAction.None,
    modifier: Modifier = Modifier
) {
    TextField(
        value = textValue,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = hint) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(Dimens.BUTTON_RADIUS)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun TextFieldPrimaryPreview() {
    ParamedQuizTheme {
        Surface {
            TextFieldPrimary(
                onValueChange = {},
                hint = "Placeholder"
            )
        }
    }
}
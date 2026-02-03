package com.rafalskrzypczyk.translation_mode.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.translation_mode.R

@Composable
fun TranslationInput(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val borderColor = when {
        isFocused -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.outlineVariant
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }
    
    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        ),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }, onNext = { onDone() }),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
                    .background(Color.Transparent)
                    .border(
                        border = BorderStroke(2.dp, borderColor),
                        shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
                    )
                    .padding(horizontal = Dimens.DEFAULT_PADDING),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty() && enabled) {
                    Text(
                        text = stringResource(R.string.input_hint_type_here),
                        style = TextStyle(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}
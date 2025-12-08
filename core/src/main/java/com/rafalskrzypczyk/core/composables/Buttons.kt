package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

@Composable
fun ButtonPrimary(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    debounced: Boolean = true,
) {
    val finalOnClick = if (debounced) rememberDebouncedClick(onClick = onClick) else onClick

    Button(
        onClick = finalOnClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Text(text = title)
    }
}

@Composable
fun ButtonSecondary(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    debounced: Boolean = true,
    icon: ImageVector? = null,
) {
    val finalOnClick = if (debounced) rememberDebouncedClick(onClick = onClick) else onClick

    OutlinedButton(
        onClick = finalOnClick,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        border = BorderStroke(Dimens.OUTLINE_THICKNESS, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if(icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
        }
        Text(text = title)
    }
}

@Composable
fun ButtonTertiary(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    debounced: Boolean = true,
) {
    val finalOnClick = if (debounced) rememberDebouncedClick(onClick = onClick) else onClick

    Button(
        onClick = finalOnClick,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = title)
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    description: String,
    enabled: Boolean = true,
    debounced: Boolean = true,
    showBackground: Boolean = true,
    onClick: () -> Unit,
) {
    val finalOnClick = if (debounced) rememberDebouncedClick(onClick = onClick) else onClick
    val containerColor = if (showBackground) MaterialTheme.colorScheme.surface else Color.Transparent

    IconButton(
        modifier = modifier,
        onClick = finalOnClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(containerColor = containerColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

@Composable
fun ActionButtonImage(
    modifier: Modifier = Modifier,
    image: Painter,
    description: String,
    enabled: Boolean = true,
    debounced: Boolean = true,
    onClick: () -> Unit,
) {
    val finalOnClick = if (debounced) rememberDebouncedClick(onClick = onClick) else onClick

    IconButton(
        modifier = modifier,
        onClick = finalOnClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = image,
            contentDescription = description
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ButtonPrimaryPreview() {
    ParamedQuizTheme {
        Surface {
            ButtonPrimary(
                title = "Placeholder",
                onClick = {},
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ButtonSecondaryPreview() {
   ParamedQuizTheme {
        Surface {
            ButtonSecondary(
                title = "Placeholder",
                onClick = {},
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ButtonTertiaryPreview() {
    ParamedQuizTheme {
        Surface {
            ButtonTertiary(
                title = "Placeholder",
                onClick = {},
            )
        }
    }
}
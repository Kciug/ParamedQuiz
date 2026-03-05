package com.rafalskrzypczyk.core.composables

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rafalskrzypczyk.core.ui.theme.Link
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun TextPrimary(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    textDecoration: TextDecoration? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    autoSize: TextAutoSize? = null
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        overflow = if (autoSize != null) TextOverflow.Clip else TextOverflow.Ellipsis,
        maxLines = maxLines,
        textAlign = textAlign,
        fontWeight = fontWeight,
        textDecoration = textDecoration,
        fontSize = fontSize,
        autoSize = autoSize
    )
}

@Composable
fun TextHeadline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = TextAlign.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    autoSize: TextAutoSize? = null
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = color,
        textAlign = textAlign,
        fontSize = fontSize,
        autoSize = autoSize
    )
}

@Composable
fun TextTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    autoSize: TextAutoSize? = null
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = if (autoSize != null) TextOverflow.Clip else TextOverflow.Ellipsis,
        autoSize = autoSize
    )
}

@Composable
fun TextCaption(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = TextAlign.Unspecified,
    fontWeight: FontWeight? = null,
    autoSize: TextAutoSize? = null
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        textAlign = textAlign,
        fontWeight = fontWeight,
        autoSize = autoSize
    )
}

@Composable
fun TextCaptionLink(
    text: String,
    url: String,
    modifier: Modifier = Modifier,
    color: Color = Link,
    textAlign: TextAlign? = TextAlign.Unspecified,
    textDecoration: TextDecoration = TextDecoration.Underline
) {
    val context = LocalContext.current

    Text(
        text = text,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
        color = color,
        textDecoration = textDecoration,
        style = MaterialTheme.typography.labelSmall,
        textAlign = textAlign
    )
}

@Composable
fun TextScore(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    autoSize: TextAutoSize? = null
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        autoSize = autoSize
    )
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun TextPrimaryPreview() {
    ParamedQuizTheme {
        Surface {
            TextPrimary(
                text = "Placeholder",
            )
        }
    }
}

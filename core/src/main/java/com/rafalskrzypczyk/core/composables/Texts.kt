package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun TextPrimary(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text
    )
}

@Composable
fun TextHeadline(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun TextCaption(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.tertiary
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

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun TextHeadlinePreview() {
    ParamedQuizTheme {
        Surface {
            TextHeadline(
                text = "Placeholder",
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun TextCaptionPreview() {
    ParamedQuizTheme {
        Surface {
            TextCaption(
                text = "Placeholder",
            )
        }
    }
}
package com.rafalskrzypczyk.home_screen.presentation.home_screen.home_page

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.NotificationDot
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun QuizModeButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Box {
        Card (
            modifier = modifier
                .padding(top = Dimens.IMAGE_SIZE_MEDIUM / 4)
                .heightIn(min = Dimens.IMAGE_SIZE_MEDIUM / 4 * 3),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            onClick = onClick,
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimens.DEFAULT_PADDING,
                        start = Dimens.DEFAULT_PADDING + Dimens.IMAGE_SIZE_MEDIUM,
                        end = Dimens.DEFAULT_PADDING,
                        bottom = Dimens.DEFAULT_PADDING
                    ),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextHeadline(title)
                TextPrimary(description)
            }
        }
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(Dimens.IMAGE_SIZE_MEDIUM)
                .padding(start = Dimens.DEFAULT_PADDING)
                .align(Alignment.TopStart)
        )
    }
}

@Composable
fun AdditionalModeButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    imageRes: Int,
    highlighted: Boolean = false,
    onClick: () -> Unit,
) {
    val highlightedColor = MaterialTheme.colorScheme.primary

    Box {
        Card (
            modifier = modifier
                .padding(top = Dimens.IMAGE_SIZE_MEDIUM / 4),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            onClick = onClick,
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
            border = if (highlighted) {
                BorderStroke(Dimens.OUTLINE_THICKNESS, highlightedColor)
            } else {
                null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimens.DEFAULT_PADDING + Dimens.IMAGE_SIZE_MEDIUM / 4 * 3,
                        start = Dimens.DEFAULT_PADDING,
                        end = Dimens.DEFAULT_PADDING,
                        bottom = Dimens.DEFAULT_PADDING
                    ),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextHeadline(title)
                description?.let {
                    TextPrimary(description)
                }
            }
        }
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(Dimens.IMAGE_SIZE_MEDIUM)
                .padding(start = Dimens.DEFAULT_PADDING)
                .align(Alignment.TopStart)
        )
        if (highlighted) {
            NotificationDot(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Dimens.IMAGE_SIZE_MEDIUM / 4)
            )

//            Icon(
//                imageVector = Icons.Default.,
//                contentDescription = null,
//                tint = highlightedColor,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(top = Dimens.IMAGE_SIZE_MEDIUM / 4)
//                    .background(
//                        color = MaterialTheme.colorScheme.surfaceContainer,
//                        shape = CircleShape
//                    )
//            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun QuizModeButtonPreview() {
    ParamedQuizTheme {
        Surface {
            QuizModeButton(
                title = "Tinder",
                description = "Umawianie sie na jebanie ale to quiz",
                imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                onClick = {}
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AdditionalModeButtonPreview() {
    ParamedQuizTheme {
        Surface {
            AdditionalModeButton(
                title = "Powtórki",
                imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                onClick = {}
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AdditionalModeButtonHighlightedPreview() {
    ParamedQuizTheme {
        Surface {
            AdditionalModeButton(
                title = "Powtórki",
                imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                onClick = {},
                highlighted = true,
            )
        }
    }
}
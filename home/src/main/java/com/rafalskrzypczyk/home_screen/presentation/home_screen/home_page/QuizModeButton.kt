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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

data class Addon(
    val title: String,
    val description: String? = null,
    val color: Color? = null,
    val imageRes: Int,
    val highlighted: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
fun AddonButton(
    modifier: Modifier = Modifier,
    addon: Addon
) {
    val highlightedColor = MaterialTheme.colorScheme.primary

    Box {
        Card (
            modifier = modifier
                .padding(top = Dimens.IMAGE_SIZE_MEDIUM / 4)
                .widthIn(min = Dimens.IMAGE_SIZE_MEDIUM + Dimens.DEFAULT_PADDING * 2),
            colors = CardDefaults.cardColors(
                containerColor = if(addon.color != null) addon.color else MaterialTheme.colorScheme.surface
            ),
            onClick = addon.onClick,
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
            border = if (addon.highlighted) {
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
                TextHeadline(addon.title)
                addon.description?.let {
                    TextPrimary(it)
                }
            }
        }
        Image(
            painter = painterResource(id = addon.imageRes),
            contentDescription = addon.title,
            modifier = Modifier
                .size(Dimens.IMAGE_SIZE_MEDIUM)
                .padding(start = Dimens.DEFAULT_PADDING)
                .align(Alignment.TopStart)
        )
        if (addon.highlighted) {
            NotificationDot(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Dimens.IMAGE_SIZE_MEDIUM / 4)
            )
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
            AddonButton(
                addon = Addon(
                    title = "Powtórki",
                    description = "Przygotuj się do quizu",
                    imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                    onClick = {}
                )
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
            AddonButton(
                addon = Addon(
                    title = "Powtórki",
                    description = "Przygotuj się do quizu",
                    imageRes = com.rafalskrzypczyk.core.R.drawable.frontfolks_logo_256,
                    highlighted = true,
                    onClick = {}
                )
            )
        }
    }
}
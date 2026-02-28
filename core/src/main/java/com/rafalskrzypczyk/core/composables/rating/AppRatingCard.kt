package com.rafalskrzypczyk.core.composables.rating

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextFieldMultiLine
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow

@Composable
fun AppRatingCard(
    modifier: Modifier = Modifier,
    state: RatingPromptState,
    feedbackText: String = "",
    onFeedbackChange: (String) -> Unit = {},
    onRate: (Int) -> Unit,
    onStoreClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    if (state == RatingPromptState.HIDDEN) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.DEFAULT_PADDING),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.DEFAULT_PADDING)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ratingCardTransition"
            ) { currentState ->
                when (currentState) {
                    RatingPromptState.QUESTION, RatingPromptState.CLOSING_OPTIONS -> {
                        RatingQuestionStep(
                            onRate = onRate,
                            enabled = enabled
                        )
                    }

                    RatingPromptState.POSITIVE_FEEDBACK -> {
                        FeedbackStep(
                            title = stringResource(R.string.rating_positive_title),
                            description = stringResource(R.string.rating_positive_desc_short),
                            icon = Icons.Default.Favorite,
                            iconColor = MQRed,
                            actionTitle = stringResource(R.string.btn_rate_store),
                            onAction = onStoreClick,
                            onBack = onBack,
                            isLoading = isLoading,
                            enabled = enabled
                        )
                    }

                    RatingPromptState.NEGATIVE_FEEDBACK -> {
                        FeedbackStep(
                            title = stringResource(R.string.rating_negative_title),
                            description = stringResource(R.string.rating_negative_desc_short),
                            icon = Icons.Default.SentimentVeryDissatisfied,
                            iconColor = MQYellow,
                            actionTitle = stringResource(R.string.btn_send_feedback),
                            onAction = onFeedbackClick,
                            onBack = onBack,
                            isLoading = isLoading,
                            enabled = enabled
                        ) {
                            TextFieldMultiLine(
                                textValue = feedbackText,
                                onValueChange = onFeedbackChange,
                                hint = stringResource(R.string.report_issue_description_hint),
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                enabled = enabled
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun RatingQuestionStep(
    onRate: (Int) -> Unit,
    enabled: Boolean = true
) {
    var rating by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.mediquiz_ic_grayscale),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.rating_question_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { index ->
                    Icon(
                        imageVector = if (index <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = MQYellow,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = enabled) {
                                rating = index
                                onRate(index)
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackStep(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    actionTitle: String,
    onAction: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    content: @Composable (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (content != null) {
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            content()
        }
        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            com.rafalskrzypczyk.core.composables.ActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                description = stringResource(R.string.btn_back),
                showBackground = false,
                onClick = onBack,
                enabled = enabled && !isLoading
            )
            ButtonPrimary(
                title = actionTitle,
                onClick = onAction,
                modifier = Modifier.weight(1f),
                loading = isLoading,
                enabled = enabled
            )
        }
    }
}

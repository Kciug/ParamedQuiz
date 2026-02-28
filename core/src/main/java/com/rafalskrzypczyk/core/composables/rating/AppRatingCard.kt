package com.rafalskrzypczyk.core.composables.rating

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonTertiary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow

@Composable
fun AppRatingCard(
    modifier: Modifier = Modifier,
    state: RatingPromptState,
    onRate: (Int) -> Unit,
    onDismiss: () -> Unit,
    onStoreClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onNeverAskAgain: () -> Unit,
    onBack: () -> Unit
) {
    if (state == RatingPromptState.HIDDEN) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.DEFAULT_PADDING)
            .padding(top = Dimens.SMALL_PADDING, bottom = Dimens.SMALL_PADDING)
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "ratingCardTransition"
        ) { currentState ->
            if (currentState == RatingPromptState.CLOSING_OPTIONS) {
                ClosingOptionsStep(onDismiss, onNeverAskAgain)
            } else {
                RatingContent(
                    state = currentState,
                    onRate = onRate,
                    onDismiss = onDismiss,
                    onStoreClick = onStoreClick,
                    onFeedbackClick = onFeedbackClick,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun RatingContent(
    state: RatingPromptState,
    onRate: (Int) -> Unit,
    onDismiss: () -> Unit,
    onStoreClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.DEFAULT_PADDING)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            if (state == RatingPromptState.POSITIVE_FEEDBACK || state == RatingPromptState.NEGATIVE_FEEDBACK) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(Dimens.DEFAULT_PADDING)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(Dimens.DEFAULT_PADDING)
                    .padding(top = Dimens.SMALL_PADDING)
                    .fillMaxWidth()
            ) {
                when (state) {
                    RatingPromptState.QUESTION -> RatingQuestionStep(onRate)
                    RatingPromptState.POSITIVE_FEEDBACK -> FeedbackStep(
                        title = stringResource(R.string.rating_positive_title),
                        description = stringResource(R.string.rating_positive_desc_short),
                        icon = Icons.Default.Favorite,
                        iconColor = MQRed,
                        actionTitle = stringResource(R.string.btn_rate_store),
                        onAction = onStoreClick
                    )
                    RatingPromptState.NEGATIVE_FEEDBACK -> FeedbackStep(
                        title = stringResource(R.string.rating_negative_title),
                        description = stringResource(R.string.rating_negative_desc_short),
                        icon = Icons.Default.SentimentVeryDissatisfied,
                        iconColor = MQYellow,
                        actionTitle = stringResource(R.string.btn_send_feedback),
                        onAction = onFeedbackClick
                    )
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun RatingQuestionStep(onRate: (Int) -> Unit) {
    var rating by remember { mutableIntStateOf(0) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
            Text(
                text = stringResource(R.string.rating_question_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
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
                        .size(48.dp)
                        .clickable {
                            rating = index
                            onRate(index)
                        }
                )
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
    onAction: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
        ButtonPrimary(
            title = actionTitle,
            onClick = onAction,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ClosingOptionsStep(onDismiss: () -> Unit, onNeverAskAgain: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ButtonTertiary(
            title = stringResource(R.string.btn_rate_later),
            onClick = onDismiss
        )
        ButtonTertiary(
            title = stringResource(R.string.btn_never_ask_again),
            onClick = onNeverAskAgain
        )
    }
}

package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.InfoDialog
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.rating.AppRatingCard
import com.rafalskrzypczyk.core.composables.top_bars.MainTopBar
import com.rafalskrzypczyk.core.ui.theme.MQBlue
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.InAppReviewManager
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.presentation.home_page.components.Addon
import com.rafalskrzypczyk.home_screen.presentation.home_page.components.HomeNewsBanner
import com.rafalskrzypczyk.home_screen.presentation.home_page.components.HomeScreenAddonsMenu
import com.rafalskrzypczyk.home_screen.presentation.home_page.components.HomeScreenQuizModesMenu
import com.rafalskrzypczyk.home_screen.presentation.home_page.components.SwipePurchaseBottomSheet
import com.rafalskrzypczyk.home_screen.presentation.home_page.components.TranslationPurchaseBottomSheet
import com.rafalskrzypczyk.score.domain.StreakState
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.imePadding

@Composable
fun HomeScreen(
    state: HomeScreenState,
    effect: kotlinx.coroutines.flow.Flow<HomeSideEffect>,
    onEvent: (HomeUIEvents) -> Unit,
    onNavigateToUserPanel: () -> Unit,
    onNavigateToDailyExercise: () -> Unit,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: (Boolean) -> Unit,
    onNavigateToTranslationMode: () -> Unit,
    onNavigateToCemMode: () -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToDevOptions: () -> Unit
) {
    var showDailyExerciseAlreadyDoneAlert by remember { mutableStateOf(false) }
    var showRevisionsUnavailableAlert by remember { mutableStateOf(false) }
    var showFeedbackSuccessAlert by remember { mutableStateOf(false) }
    var isDismissingMode by remember { mutableStateOf<String?>(null) }
    var isTrialMode by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val reviewManager = remember(context) { InAppReviewManager(context) }
    val scrollState = rememberScrollState()

    val addons = listOf(
        Addon(
            title = stringResource(R.string.title_addon_daily_exercises),
            icon = Icons.Rounded.Bolt,
            iconBackgroundColor = MQRed,
            highlighted = state.isNewDailyExerciseAvailable,
            isAvailable = state.isNewDailyExerciseAvailable
        ) {
            if (state.isNewDailyExerciseAvailable) {
                onNavigateToDailyExercise()
            } else {
                showDailyExerciseAlreadyDoneAlert = true
            }
        },
        Addon(
            title = stringResource(R.string.title_addon_review),
            icon = Icons.Rounded.History,
            iconBackgroundColor = MQBlue,
            isAvailable = false,
        ) { showRevisionsUnavailableAlert = true },
        Addon(
            title = stringResource(R.string.title_store),
            icon = Icons.Default.Diamond,
            iconBackgroundColor = MQYellow,
            highlighted = !state.isPremium,
            isAvailable = true
        ) { onNavigateToStore() },
    )

    LaunchedEffect(Unit) {
        onEvent.invoke(HomeUIEvents.GetData)
    }

    LaunchedEffect(state.ratingPromptState) {
        if (state.ratingPromptState == com.rafalskrzypczyk.core.composables.rating.RatingPromptState.NEGATIVE_FEEDBACK) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    LaunchedEffect(effect) {
        effect.collectLatest { sideEffect ->
            when(sideEffect) {
                is HomeSideEffect.PurchaseSuccess -> {
                }
                HomeSideEffect.LaunchReviewFlow -> {
                    activity?.let { reviewManager.launchReviewFlow(it) }
                }
                HomeSideEffect.FeedbackSuccess -> {
                    showFeedbackSuccessAlert = true
                }
            }
        }
    }
    
    if (state.showTranslationModePurchaseSheet) {
        TranslationPurchaseBottomSheet(
            isUnlocked = state.isTranslationModeUnlocked,
            isPurchasing = state.isPurchasing,
            purchaseError = state.purchaseError,
            questionCount = state.translationModeQuestionCount,
            price = state.translationModePrice,
            activity = activity,
            shouldDismiss = isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_TRANSLATION_MODE,
            onDismiss = { 
                onEvent(HomeUIEvents.CloseTranslationModePurchaseSheet)
                if (isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_TRANSLATION_MODE) {
                    onNavigateToTranslationMode()
                    isDismissingMode = null
                }
            },
            onBuyClick = { act -> onEvent(HomeUIEvents.BuyTranslationMode(act)) },
            onStartClick = { 
                isTrialMode = false
                isDismissingMode = com.rafalskrzypczyk.billing.domain.BillingIds.ID_TRANSLATION_MODE 
            }
        )
    }

    if (state.showSwipeModePurchaseSheet) {
        SwipePurchaseBottomSheet(
            isUnlocked = state.isSwipeModeUnlocked,
            isPurchasing = state.isPurchasing,
            purchaseError = state.purchaseError,
            questionCount = state.swipeModeQuestionCount,
            price = state.swipeModePrice,
            activity = activity,
            shouldDismiss = isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE,
            onDismiss = {
                onEvent(HomeUIEvents.CloseSwipeModePurchaseSheet)
                if (isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE) {
                    onNavigateToSwipeMode(isTrialMode)
                    isDismissingMode = null
                    isTrialMode = false
                }
            },
            onBuyClick = { act -> onEvent(HomeUIEvents.BuySwipeMode(act)) },
            onStartClick = {
                isTrialMode = false
                isDismissingMode = com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE
            },
            onTryClick = {
                isTrialMode = true
                isDismissingMode = com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MainTopBar(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                isPremium = state.isPremium,
                userAvatar = state.userAvatar,
                userStreakPending = state.userStreakState == StreakState.PENDING,
                onClick = onNavigateToDevOptions
            ) { onNavigateToUserPanel() }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
            )

        Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeCard(
                userName = state.userName,
                modifier = Modifier
                    .padding(horizontal = Dimens.DEFAULT_PADDING)
                    .padding(top = Dimens.DEFAULT_PADDING)
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = state.newsBanners.isNotEmpty(),
                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.DEFAULT_PADDING)
                        .padding(top = Dimens.DEFAULT_PADDING)
                ) {
                    TextHeadline(
                        text = stringResource(R.string.title_news_section),
                        modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING_SMALL)
                    )
                    
                    androidx.compose.animation.AnimatedContent(
                        targetState = state.newsBanners.firstOrNull(),
                        transitionSpec = {
                            (androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(initialScale = 0.95f))
                                .togetherWith(androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut(targetScale = 0.95f))
                        },
                        label = "NewsBannerAnimation"
                    ) { banner ->
                        banner?.let {
                            HomeNewsBanner(
                                banner = it,
                                onDismiss = { onEvent(HomeUIEvents.DismissNews(it.id)) }
                            )
                        }
                    }
                }
            }

            HomeScreenAddonsMenu(addons = addons)
            
            androidx.compose.animation.AnimatedVisibility(
                visible = state.ratingPromptState != com.rafalskrzypczyk.core.composables.rating.RatingPromptState.HIDDEN,
                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.DEFAULT_PADDING)
                            .padding(top = Dimens.DEFAULT_PADDING),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextHeadline(
                            text = stringResource(R.string.title_rating_section),
                        )
                        IconButton(
                            onClick = { onEvent(HomeUIEvents.OnDismissRating) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
                    AppRatingCard(
                        state = state.ratingPromptState,
                        feedbackText = state.feedbackText,
                        onFeedbackChange = { feedback -> onEvent(HomeUIEvents.OnFeedbackChanged(feedback)) },
                        onRate = { rating -> onEvent(HomeUIEvents.OnRatingSelected(rating)) },
                        onStoreClick = { onEvent(HomeUIEvents.OnRateStore) },
                        onFeedbackClick = { onEvent(HomeUIEvents.OnSendFeedback) },
                        onBack = { onEvent(HomeUIEvents.OnBackToRating) },
                        isLoading = state.isSendingFeedback,
                        enabled = !state.isSendingFeedback
                    )
                }
            }

            if (state.ratingPromptState == com.rafalskrzypczyk.core.composables.rating.RatingPromptState.CLOSING_OPTIONS) {
                com.rafalskrzypczyk.core.composables.BaseCustomDialog(
                    onDismissRequest = { onEvent(HomeUIEvents.OnBackToRating) },
                    icon = Icons.AutoMirrored.Default.HelpOutline,
                    title = stringResource(com.rafalskrzypczyk.core.R.string.rating_dismiss_title),
                    content = {
                        TextPrimary(
                            text = stringResource(com.rafalskrzypczyk.core.R.string.rating_dismiss_desc),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    buttons = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                            horizontalAlignment = Alignment.End
                        ) {
                            androidx.compose.material3.TextButton(
                                onClick = { onEvent(HomeUIEvents.OnDismissRating) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextPrimary(
                                    text = stringResource(com.rafalskrzypczyk.core.R.string.btn_dismiss_later),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            androidx.compose.material3.TextButton(
                                onClick = { onEvent(HomeUIEvents.OnNeverAskAgain) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextPrimary(
                                    text = stringResource(com.rafalskrzypczyk.core.R.string.btn_dismiss_never),
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                )
            }

            HomeScreenQuizModesMenu(
                isTranslationModeUnlocked = state.isTranslationModeUnlocked,
                isSwipeModeUnlocked = state.isSwipeModeUnlocked,
                onNavigateToMainMode = onNavigateToMainMode,
                onNavigateToSwipeMode = {
                    onEvent(HomeUIEvents.OpenSwipeModePurchaseSheet)
                },
                onNavigateToTranslationMode = {
                    onEvent(HomeUIEvents.OpenTranslationModePurchaseSheet)
                },
                onNavigateToCemMode = onNavigateToCemMode
            )

            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)))
        }
    }

    if(showDailyExerciseAlreadyDoneAlert) {
        InfoDialog(
            title = stringResource(id = R.string.title_daily_exercise_already_done),
            message = stringResource(id = R.string.text_daily_exercise_already_done),
            icon = Icons.Default.Check,
            headerColor = MQGreen,
            onDismiss = { showDailyExerciseAlreadyDoneAlert = false }
        )
    }

    if(showRevisionsUnavailableAlert) {
        InfoDialog(
            title = stringResource(id = R.string.title_revisions_unavailable),
            message = stringResource(id = R.string.text_revisions_unavailable),
            icon = Icons.Default.Upcoming,
            headerColor = MQYellow,
            headerContentColor = Color.Black,
            onDismiss = { showRevisionsUnavailableAlert = false }
        )
    }

    if(showFeedbackSuccessAlert) {
        InfoDialog(
            title = stringResource(id = com.rafalskrzypczyk.core.R.string.desc_success),
            message = stringResource(id = com.rafalskrzypczyk.core.R.string.rating_feedback_success),
            icon = Icons.Default.Favorite,
            headerColor = MQRed,
            onDismiss = { 
                showFeedbackSuccessAlert = false 
                onEvent(HomeUIEvents.OnFeedbackSuccessConsumed)
            }
        )
    }

    if(state.feedbackErrorMessage != null) {
        InfoDialog(
            title = stringResource(id = com.rafalskrzypczyk.core.R.string.desc_error),
            message = state.feedbackErrorMessage,
            icon = Icons.Default.PriorityHigh,
            headerColor = MQRed,
            onDismiss = { 
                onEvent(HomeUIEvents.OnFeedbackErrorConsumed)
            }
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(heightDp = 360, widthDp = 800)
@Composable
private fun HomeScreenPreview() {
    ParamedQuizTheme {
        Surface {
            HomeScreen(
                state = HomeScreenState(
                    isUserLoggedIn = true,
                    userScore = 95600,
                    userStreak = 24,
                    isNewDailyExerciseAvailable = true,
                    isTranslationModeUnlocked = false,
                    isSwipeModeUnlocked = false
                ),
                effect = kotlinx.coroutines.flow.emptyFlow(),
                onEvent = {},
                onNavigateToUserPanel = {},
                onNavigateToMainMode = {},
                onNavigateToSwipeMode = {},
                onNavigateToTranslationMode = {},
                onNavigateToCemMode = {},
                onNavigateToStore = {},
                onNavigateToDailyExercise = {},
                onNavigateToDevOptions = {}
            )
        }
    }
}

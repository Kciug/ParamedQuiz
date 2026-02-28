package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.rafalskrzypczyk.core.composables.BasePurchaseBottomSheet
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.InfoDialog
import com.rafalskrzypczyk.core.composables.PurchaseFeature
import com.rafalskrzypczyk.core.composables.PurchaseModeDetails
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.rating.AppRatingCard
import com.rafalskrzypczyk.core.composables.top_bars.MainTopBar
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.InAppReviewManager
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.score.domain.StreakState
import kotlinx.coroutines.flow.collectLatest

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
    onNavigateToDevOptions: () -> Unit
) {
    var showDailyExerciseAlreadyDoneAlert by remember { mutableStateOf(false) }
    var showRevisionsUnavailableAlert by remember { mutableStateOf(false) }
    var isDismissingMode by remember { mutableStateOf<String?>(null) }
    var isTrialMode by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val reviewManager = remember(context) { InAppReviewManager(context) }

    val addons = listOf(
        Addon(
            title = stringResource(R.string.title_addon_daily_exercises),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_dailyexercise,
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
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_revisions,
            isAvailable = false,
        ) { showRevisionsUnavailableAlert = true },
    )

    LaunchedEffect(Unit) {
        onEvent.invoke(HomeUIEvents.GetData)
    }

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when(effect) {
                is HomeSideEffect.PurchaseSuccess -> {
                    // Just let the UI update via state
                }
                HomeSideEffect.LaunchReviewFlow -> {
                    activity?.let { reviewManager.launchReviewFlow(it) }
                }
                HomeSideEffect.OpenFeedbackMail -> {
                    sendFeedbackEmail(context)
                }
            }
        }
    }
    
    if (state.showTranslationModePurchaseSheet) {
        BasePurchaseBottomSheet(
            onDismiss = { 
                onEvent(HomeUIEvents.CloseTranslationModePurchaseSheet)
                if (isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_TRANSLATION_MODE) {
                    onNavigateToTranslationMode()
                    isDismissingMode = null
                }
            },
            onBuyClick = { 
                if (activity != null) {
                    onEvent(HomeUIEvents.BuyTranslationMode(activity))
                }
            },
            onStartClick = { 
                isTrialMode = false
                isDismissingMode = com.rafalskrzypczyk.billing.domain.BillingIds.ID_TRANSLATION_MODE 
            },
            isUnlocked = state.isTranslationModeUnlocked,
            isPurchasing = state.isPurchasing,
            purchaseError = state.purchaseError,
            shouldDismiss = isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_TRANSLATION_MODE,
            details = PurchaseModeDetails(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
                description = stringResource(R.string.mode_translation_desc),
                questionCount = state.translationModeQuestionCount,
                price = state.translationModePrice,
                features = listOf(
                    PurchaseFeature(
                        title = stringResource(R.string.feature_translation_title),
                        description = stringResource(R.string.feature_translation_desc),
                        icon = Icons.Default.Translate
                    ),
                    PurchaseFeature(
                        title = stringResource(R.string.feature_vocabulary_title),
                        description = stringResource(R.string.feature_vocabulary_desc),
                        icon = Icons.Default.HistoryEdu
                    ),
                    PurchaseFeature(
                        title = stringResource(R.string.feature_auto_fix_title),
                        description = stringResource(R.string.feature_auto_fix_desc),
                        icon = Icons.Default.AutoFixHigh
                    )
                )
            )
        )
    }

    if (state.showSwipeModePurchaseSheet) {
        BasePurchaseBottomSheet(
            onDismiss = {
                onEvent(HomeUIEvents.CloseSwipeModePurchaseSheet)
                if (isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE) {
                    onNavigateToSwipeMode(isTrialMode)
                    isDismissingMode = null
                    isTrialMode = false
                }
            },
            onBuyClick = {
                if (activity != null) {
                    onEvent(HomeUIEvents.BuySwipeMode(activity))
                }
            },
            onStartClick = {
                isTrialMode = false
                isDismissingMode = com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE
            },
            onTryClick = {
                isTrialMode = true
                isDismissingMode = com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE
            },
            isUnlocked = state.isSwipeModeUnlocked,
            isPurchasing = state.isPurchasing,
            purchaseError = state.purchaseError,
            shouldDismiss = isDismissingMode == com.rafalskrzypczyk.billing.domain.BillingIds.ID_SWIPE_MODE,
            details = PurchaseModeDetails(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                description = stringResource(R.string.mode_swipe_desc),
                questionCount = state.swipeModeQuestionCount,
                price = state.swipeModePrice,
                features = listOf(
                    PurchaseFeature(
                        title = stringResource(R.string.feature_swipe_title),
                        description = stringResource(R.string.feature_swipe_desc),
                        icon = Icons.Default.Swipe
                    ),
                    PurchaseFeature(
                        title = stringResource(R.string.feature_speed_title),
                        description = stringResource(R.string.feature_speed_desc),
                        icon = Icons.Default.Bolt
                    ),
                    PurchaseFeature(
                        title = stringResource(R.string.feature_combo_title),
                        description = stringResource(R.string.feature_combo_desc),
                        icon = Icons.Default.Whatshot
                    )
                )
            )
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
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeCard(
                userName = state.userName,
                modifier = Modifier
                    .padding(horizontal = Dimens.DEFAULT_PADDING)
                    .padding(top = Dimens.DEFAULT_PADDING)
            )
            HomeScreenAddonsMenu(addons = addons)
            
            androidx.compose.animation.AnimatedVisibility(
                visible = state.ratingPromptState != com.rafalskrzypczyk.core.composables.rating.RatingPromptState.HIDDEN,
                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
            ) {
                Column {
                    TextHeadline(
                        text = stringResource(R.string.title_rating_section),
                        modifier = Modifier.padding(start = Dimens.DEFAULT_PADDING, top = Dimens.DEFAULT_PADDING)
                    )
                    AppRatingCard(
                        state = state.ratingPromptState,
                        onRate = { rating -> onEvent(HomeUIEvents.OnRatingSelected(rating)) },
                        onDismiss = { onEvent(HomeUIEvents.OnDismissRating) },
                        onStoreClick = { onEvent(HomeUIEvents.OnRateStore) },
                        onFeedbackClick = { onEvent(HomeUIEvents.OnSendFeedback) },
                        onNeverAskAgain = { onEvent(HomeUIEvents.OnNeverAskAgain) },
                        onBack = { onEvent(HomeUIEvents.OnBackToRating) }
                    )
                }
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
                }
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
}

private fun sendFeedbackEmail(context: Context) {
    val contactMail = context.getString(com.rafalskrzypczyk.core.R.string.contact_mail)
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:${contactMail}".toUri()
        putExtra(Intent.EXTRA_SUBJECT, com.rafalskrzypczyk.core.R.string.feedback_mail_topic)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Wyślij opinię…"))
    } catch (_: Exception) {}
}

@Composable
fun HomeScreenAddonsMenu(
    modifier: Modifier = Modifier,
    addons: List<Addon>
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextHeadline(
            text = stringResource(R.string.title_addons),
            modifier = Modifier.padding(start = Dimens.DEFAULT_PADDING, top = Dimens.DEFAULT_PADDING)
        )
        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.DEFAULT_PADDING),
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            items(addons, key = { it.title }) { addon ->
                AddonButton(
                    //modifier = cardWidthModifier,
                    addon = addon
                )
            }
        }
    }
}

@Composable
fun HomeScreenQuizModesMenu(
    modifier: Modifier = Modifier,
    isTranslationModeUnlocked: Boolean,
    isSwipeModeUnlocked: Boolean,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: (Boolean) -> Unit,
    onNavigateToTranslationMode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
    ) {
        TextHeadline(stringResource(R.string.title_quiz_modes))
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
            description = stringResource(R.string.mode_quiz_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_mainmode
        ) { onNavigateToMainMode() }
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
            description = stringResource(R.string.mode_swipe_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_swipemode
        ) { onNavigateToSwipeMode(false) }
        QuizModeButton(
            title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
            description = stringResource(R.string.mode_translation_desc),
            imageRes = com.rafalskrzypczyk.core.R.drawable.mediquiz_translations,
            locked = !isTranslationModeUnlocked
        ) { onNavigateToTranslationMode() }
        Card (
            modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                Color.Transparent
                            ),
                            endY = 100f
                        ),
                    )
                    .padding(
                        horizontal = Dimens.DEFAULT_PADDING,
                        vertical = Dimens.DEFAULT_PADDING * 2
                    ),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextHeadline(
                    text = stringResource(R.string.more_modes_soon),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
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
                onNavigateToDailyExercise = {},
                onNavigateToDevOptions = {}
            )
        }
    }
}

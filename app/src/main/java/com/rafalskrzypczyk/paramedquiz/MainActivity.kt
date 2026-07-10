package com.rafalskrzypczyk.paramedquiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.core.ads.AdManager
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.notifications.NotificationDestination
import com.rafalskrzypczyk.paramedquiz.navigation.AppNavHost
import com.rafalskrzypczyk.paramedquiz.navigation.navigateToMainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.navigateToRevisionsMode
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var scoreManager: ScoreManager

    @Inject
    lateinit var sharedPreferences: SharedPreferencesApi

    @Inject
    lateinit var adManager: AdManager

    private val viewModel: MainActivityVM by viewModels()

    /** Cel deep-linku z tapniętego powiadomienia; kolekcjonowany w [Navigation]. */
    private val deepLinkDestination = MutableStateFlow<NotificationDestination?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (sharedPreferences.getInstallDate() == 0L) {
            sharedPreferences.setInstallDate(System.currentTimeMillis())
        }

        // Deep-link obsługujemy tylko przy świeżym starcie. Przy odtworzeniu Activity
        // (savedInstanceState != null) back stack jest już przywracany przez NavHost —
        // ponowna nawigacja z zachowanego intentu zostawiałaby pusty NavHost (biały ekran).
        if (savedInstanceState == null) {
            handleDeepLinkIntent(intent)
        }

        enableEdgeToEdge()
        setContent {
            ParamedQuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state = viewModel.state.collectAsState().value
                    var showErrorDialog by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        scoreManager.errorFlow.collect { msg ->
                            errorMessage = msg
                            showErrorDialog = true
                        }
                    }

                    if (!state.isLoading && state.startDestination != null) {
                        Navigation(state.startDestination)
                    }

                    if (showErrorDialog) {
                        ErrorDialog(errorMessage) {
                            showErrorDialog = false
                            errorMessage = ""
                        }
                    }
                }
            }
        }

        adManager.initialize(this)

        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.isLoading
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLinkIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        scoreManager.forceSync()
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        val destination = NotificationDestination.fromExtra(
            intent?.getStringExtra(NotificationDestination.EXTRA_DESTINATION)
        ) ?: return
        deepLinkDestination.value = destination
        // Konsumujemy extra, żeby zachowany intent nie odpalił deep-linku ponownie
        // przy kolejnym odtworzeniu Activity.
        intent?.let {
            it.removeExtra(NotificationDestination.EXTRA_DESTINATION)
            setIntent(it)
        }
    }

    @Composable
    private fun Navigation(startDestination: Any) {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collect { destination ->
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        LaunchedEffect(Unit) {
            deepLinkDestination.collect { destination ->
                when (destination) {
                    NotificationDestination.HOME -> navController.navigateToMainMenu()
                    NotificationDestination.REVISIONS -> navController.navigateToRevisionsMode()
                    null -> Unit
                }
                // Skonsumuj cel, żeby nie nawigować ponownie przy recompositions.
                if (destination != null) deepLinkDestination.value = null
            }
        }

        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            isOnboarding = { getOnboardingState() },
            onFinishOnboarding = { onFinishOnboarding() }
        )
    }

    private fun getOnboardingState(): Boolean = !sharedPreferences.getOnboardingStatus()

    private fun onFinishOnboarding() {
        viewModel.onEvent(MainActivityUIEvents.OnboardingFinished)
    }
}

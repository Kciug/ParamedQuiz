package com.rafalskrzypczyk.paramedquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.paramedquiz.navigation.AppNavHost
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var scoreManager: ScoreManager

    @Inject
    lateinit var sharedPreferences: SharedPreferencesApi

    private val viewModel: MainActivityVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.isLoading
        }

        enableEdgeToEdge()
        setContent {
            ParamedQuizTheme {
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

    override fun onStop() {
        super.onStop()
        scoreManager.forceSync()
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


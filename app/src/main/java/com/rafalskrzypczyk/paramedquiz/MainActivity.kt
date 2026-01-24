package com.rafalskrzypczyk.paramedquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        enableEdgeToEdge()
        setContent {
            ParamedQuizTheme {
                var showErrorDialog by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    scoreManager.errorFlow.collect { msg ->
                        errorMessage = msg
                        showErrorDialog = true
                    }
                }

                Navigation {
                    keepSplashScreen = false
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
    private fun Navigation(onDataLoaded: () -> Unit) {
        val navController = rememberNavController()
        AppNavHost(
            navController = navController,
            isOnboarding = { getOnboardingState() },
            onFinishOnboarding = { onFinishOnboarding() },
            onDataLoaded = onDataLoaded
        )
    }

    private fun getOnboardingState(): Boolean = !sharedPreferences.getOnboardingStatus()

    private fun onFinishOnboarding() {
        sharedPreferences.setOnboardingStatus(true)
    }
}


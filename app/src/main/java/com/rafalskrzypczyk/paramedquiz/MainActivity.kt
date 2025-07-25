package com.rafalskrzypczyk.paramedquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.paramedquiz.navigation.AppNavHost
import com.rafalskrzypczyk.score.ScoreManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var scoreManager: ScoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParamedQuizTheme {
                Navigation()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        scoreManager.forceSync()
    }

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        AppNavHost(navController) { authRepository.isUserLoggedIn() }
    }
}


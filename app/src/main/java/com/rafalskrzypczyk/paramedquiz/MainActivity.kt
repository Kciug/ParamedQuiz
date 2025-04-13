package com.rafalskrzypczyk.paramedquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.paramedquiz.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParamedQuizTheme {
                Navigation()
            }
        }
    }

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        AppNavHost(navController)
    }
}


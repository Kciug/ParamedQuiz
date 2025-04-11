package com.rafalskrzypczyk.paramedquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.paramedquiz.navigation.AppNavHost
import com.rafalskrzypczyk.paramedquiz.ui.theme.ParamedQuizTheme

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


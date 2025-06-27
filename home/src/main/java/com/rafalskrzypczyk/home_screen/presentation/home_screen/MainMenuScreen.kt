package com.rafalskrzypczyk.home_screen.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.UserProfileIconAction
import com.rafalskrzypczyk.core.ui.TitleTopBar

@Composable
fun MainMenuScreen(
    onNavigateToUserPanel: () -> Unit,
    onNavigateToMainMode: () -> Unit,
    onNavigateToSwipeMode: () -> Unit
) {
    Scaffold (
        topBar = {
            TitleTopBar(stringResource(R.string.app_name)) {
                UserProfileIconAction(
                    userImage = null,
                    onNavigateToUserProfile = onNavigateToUserPanel
                )
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            ){
                MainMenuButton(
                    buttonText = "Quiz",
                    buttonAction = { onNavigateToMainMode() }
                )
                MainMenuButton(
                    buttonText = "Wiedza w akcji",
                    buttonAction = { onNavigateToSwipeMode() }
                )
                MainMenuButton(
                    buttonText = "Obliczenia",
                    buttonAction = { }
                )
                MainMenuButton(
                    buttonText = "Scenariusze",
                    buttonAction = { }
                )
            }
        }
    }
}

@Composable
fun MainMenuButton(buttonText: String, buttonAction: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.Transparent)
            .clickable(
                onClick = {
                    buttonAction()
                }
            )
    ) {
        Text(
            text = buttonText,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun MainMenuPreview() {
    Surface {
        MainMenuScreen(
            onNavigateToUserPanel = {},
            onNavigateToMainMode = {},
            onNavigateToSwipeMode = {}
        )
    }
}
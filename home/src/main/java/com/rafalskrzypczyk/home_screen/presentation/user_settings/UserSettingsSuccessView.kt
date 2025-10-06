package com.rafalskrzypczyk.home_screen.presentation.user_settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.Dimens

data class UserSettingsSuccessContent(
    val message: String = "",
    val icon: ImageVector = Icons.Default.SentimentSatisfiedAlt
)

@Composable
fun UserSettingsSuccessView(
    modifier: Modifier,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SentimentSatisfiedAlt,
            contentDescription = null
        )
        Text(text = "Operacja udana")
        ButtonSecondary(
            title = "OK",
            onClick = onConfirm,
        )

    }
}
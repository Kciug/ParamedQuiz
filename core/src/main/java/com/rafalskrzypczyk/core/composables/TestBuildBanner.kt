package com.rafalskrzypczyk.core.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat
import androidx.compose.material3.Text
import com.rafalskrzypczyk.core.BuildConfig
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.adaptiveContentColor

/**
 * Jaskrawy baner oznaczajacy build testowy (debug/staging). Na produkcji (release)
 * flaga [BuildConfig.DEV_OPTIONS_ENABLED] jest false i baner nie jest renderowany.
 * Celowo krzykliwy - ma jednoznacznie sygnalizowac, ze to nie jest wersja produkcyjna.
 */
@Composable
fun TestBuildBanner(modifier: Modifier = Modifier) {
    if (!BuildConfig.DEV_OPTIONS_ENABLED) return

    val context = LocalContext.current
    val versionLabel = remember {
        runCatching {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${PackageInfoCompat.getLongVersionCode(packageInfo)})"
        }.getOrNull().orEmpty()
    }

    val contentColor = MQRed.adaptiveContentColor()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.DEFAULT_PADDING)
            .clip(RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .background(MQRed)
            .padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠ BUILD TESTOWY ⚠",
            color = contentColor,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        if (versionLabel.isNotBlank()) {
            Text(
                text = versionLabel,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

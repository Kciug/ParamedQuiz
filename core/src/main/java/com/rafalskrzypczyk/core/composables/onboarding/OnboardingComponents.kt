package com.rafalskrzypczyk.core.composables.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary

@Composable
fun OnboardingIconComposition(
    mainIcon: ImageVector,
    mainIconColor: Color,
    secondaryIcons: List<ImageVector> = emptyList(),
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(260.dp)
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(
                    color = mainIconColor.copy(alpha = 0.12f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    color = mainIconColor.copy(alpha = 0.06f),
                    shape = CircleShape
                )
        )
        
        val offsets = listOf(
            Pair(105.dp, (-55).dp),
            Pair((-110).dp, 25.dp),
            Pair(45.dp, 90.dp),
            Pair((-35).dp, (-100).dp)
        )

        secondaryIcons.forEachIndexed { index, icon ->
            val offset = offsets.getOrNull(index) ?: Pair(0.dp, 0.dp)
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = offset.first, y = offset.second)
                    .size(48.dp)
                    .background(
                        color = mainIconColor.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = mainIconColor.copy(alpha = 0.45f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Icon(
            imageVector = mainIcon,
            contentDescription = null,
            tint = mainIconColor,
            modifier = Modifier
                .size(85.dp)
        )
    }
}

@Composable
fun OnboardingPage(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    iconCompose: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.LARGE_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        iconCompose()
        
        Spacer(Modifier.height(Dimens.LARGE_PADDING))
        
        TextHeadline(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(Dimens.ELEMENTS_SPACING))
        
        TextPrimary(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnboardingPageIndicator(
    modifier: Modifier = Modifier,
    pagesCount: Int,
    currentPage: Int,
    dotSize: Dp = Dimens.NOTIFICATION_DOT,
    activeColor: Color,
    inactiveColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING),
        modifier = modifier
    ) {
        repeat(pagesCount) { index ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(
                        color = if (index == currentPage) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

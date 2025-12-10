package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    landscapeRatio: Float = 0.5f,
    landscapeSpacing: Dp = Dimens.ELEMENTS_SPACING,
    content: @Composable AdaptiveLayoutScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scope = remember(isLandscape) {
        AdaptiveLayoutScopeImpl(isLandscape)
    }

    if (isLandscape) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(landscapeSpacing)
        ) {
            Box(modifier = Modifier.weight(landscapeRatio)) {
                scope.leftContent?.invoke()
            }
            Box(modifier = Modifier.weight(1f - landscapeRatio)) {
                scope.rightContent?.invoke()
            }
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            scope.content()
        }
    }
}

interface AdaptiveLayoutScope {
    val isLandscape: Boolean

    @Composable
    fun LeftColumn(content: @Composable () -> Unit)

    @Composable
    fun RightColumn(content: @Composable () -> Unit)

    @Composable
    fun FullWidth(content: @Composable () -> Unit)
}

private class AdaptiveLayoutScopeImpl(
    override val isLandscape: Boolean
) : AdaptiveLayoutScope {
    var leftContent: (@Composable () -> Unit)? = null
    var rightContent: (@Composable () -> Unit)? = null

    @Composable
    override fun LeftColumn(content: @Composable () -> Unit) {
        if (isLandscape) {
            leftContent = content
        } else {
            content()
        }
    }

    @Composable
    override fun RightColumn(content: @Composable () -> Unit) {
        if (isLandscape) {
            rightContent = content
        } else {
            content()
        }
    }

    @Composable
    override fun FullWidth(content: @Composable () -> Unit) {
        content()
    }
}

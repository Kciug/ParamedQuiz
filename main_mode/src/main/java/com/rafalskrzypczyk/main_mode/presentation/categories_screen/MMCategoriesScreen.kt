package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.UserProfileIconAction
import com.rafalskrzypczyk.core.ui.NavigationTopBar
import com.rafalskrzypczyk.main_mode.R

@Composable
fun MMCategoriesScreen(
    state: MMCategoriesState,
    onEvent: (MMCategoriesUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onUserPanel: () -> Unit,
    onStartCategory: (Long, String) -> Unit
) {
    LaunchedEffect(Unit) {
        onEvent(MMCategoriesUIEvents.GetData)
    }

    Scaffold(
        topBar = {
            NavigationTopBar(
                title = stringResource(R.string.title_categories),
                onNavigateBack = onNavigateBack,
                actions = {
                    UserProfileIconAction(
                        userImage = state.userIcon,
                        onNavigateToUserProfile = onUserPanel
                    )
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when(state.responseState) {
            ResponseState.Idle -> {}
            ResponseState.Loading -> Loading()
            is ResponseState.Error -> ErrorDialog(state.responseState.message) { onNavigateBack() }
            ResponseState.Success -> MMCategoriesScreenContent(
                modifier = modifier,
                categories = state.categories,
                onStartCategory = onStartCategory,
                onUnlockCategory = { onEvent(MMCategoriesUIEvents.OnUnlockCategory(it)) }
            )
        }
    }
}

@Composable
private fun MMCategoriesScreenContent(
    modifier: Modifier = Modifier,
    categories: List<CategoryUIM>,
    onStartCategory: (Long, String) -> Unit,
    onUnlockCategory: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING),
    ) {
        categories.forEach { category ->
            item {
                CategoryItem(
                    category = category,
                    onStartCategory = onStartCategory,
                    onUnlockCategory = onUnlockCategory
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryUIM,
    onStartCategory: (Long, String) -> Unit,
    onUnlockCategory: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                Dimens.OUTLINE_THICKNESS,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(Dimens.RADIUS_DEFAULT)
            )
            .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .clickable { onStartCategory(category.id, category.title) }
            .padding(Dimens.DEFAULT_PADDING)
    ) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        if (category.subtitle.isNullOrEmpty().not()) {
            Text(category.subtitle)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.DEFAULT_PADDING))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = stringResource(R.string.desc_questions_number),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = category.questionCount.toString(),
                    modifier = Modifier.padding(start = Dimens.SMALL_PADDING)
                )
            }
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = stringResource(R.string.desc_category_locked),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


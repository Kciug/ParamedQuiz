package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.user_management.CheckIsUserLoggedInUC
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.cem_mode.domain.use_cases.CemCategoriesUseCases
import com.rafalskrzypczyk.cem_mode.navigation.CemCategoriesRoute
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakStateUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CemCategoriesVM @Inject constructor(
    private val useCases: CemCategoriesUseCases,
    private val getUserScoreUC: GetUserScoreUC,
    private val getStreakStateUC: GetStreakStateUC,
    private val checkIsUserLoggedInUC: CheckIsUserLoggedInUC,
    private val premiumStatusProvider: PremiumStatusProvider,
    private val userManager: UserManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<CemCategoriesRoute>()
    private val parentId = route.parentId
    private val categoryTitle = route.categoryTitle

    private val _state = MutableStateFlow(CemCategoriesState(title = categoryTitle))
    val state = _state.asStateFlow()

    init {
        loadCategories()
        loadParentCategory()
        observeCategoriesUpdate()
        observeUserData()
        observePremiumStatus()
    }

    fun onEvent(event: CemCategoriesUIEvents) {
        when (event) {
            CemCategoriesUIEvents.OnRetry -> {
                loadCategories()
                loadParentCategory()
            }
        }
    }

    private fun loadParentCategory() {
        if (parentId == 0L) return

        viewModelScope.launch {
            useCases.getCemCategory(parentId).collectLatest { response ->
                if (response is Response.Success) {
                    _state.update { it.copy(parentCategory = response.data.toUIM()) }
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            useCases.getCemCategories(parentId).collectLatest { response ->
                val result = when (response) {
                    is Response.Success -> Response.Success(response.data.map { it.toUIM() })
                    is Response.Error -> Response.Error(response.error)
                    Response.Loading -> Response.Loading
                }
                _state.update { it.copy(categories = result) }
            }
        }
    }

    private fun observeCategoriesUpdate() {
        viewModelScope.launch {
            useCases.getUpdatedCemCategories(parentId).collectLatest { list ->
                _state.update { it.copy(categories = Response.Success(list.map { it.toUIM() })) }
            }
        }
    }

    private fun observeUserData() {
        viewModelScope.launch {
            getUserScoreUC().collectLatest { score ->
                _state.update {
                    it.copy(
                        userScore = score.score,
                        userStreak = score.streak,
                        userStreakState = getStreakStateUC(score.lastStreakUpdateDate),
                        isUserLoggedIn = checkIsUserLoggedInUC(),
                        userAvatar = userManager.getCurrentLoggedUser()?.name // Or avatar URL if available
                    )
                }
            }
        }
    }

    private fun observePremiumStatus() {
        viewModelScope.launch {
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val hasFull = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                _state.update { it.copy(isPremium = hasFull) }
            }
        }
    }
}

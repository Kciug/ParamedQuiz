package com.rafalskrzypczyk.home_screen.presentation.home_screen.home_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenVM @Inject constructor(
    private val userManager: UserManager,
    private val getUserScoreUC: GetUserScoreUC,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getUserScoreUC.invoke().collect {
                _state.value = _state.value.copy(
                    userScore = it.score.toInt(),
                    userStreak = it.streak.toInt(),
                    isUserLoggedIn = userManager.getCurrentLoggedUser() != null
                )
            }
        }
    }
}
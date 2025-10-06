package com.rafalskrzypczyk.home_screen.presentation.home_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.home_screen.domain.CheckDailyExerciseAvailabilityUC
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakStateUC
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
    private val getStreakStateUC: GetStreakStateUC,
    private val checkDailyExerciseAvailabilityUC: CheckDailyExerciseAvailabilityUC
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
                    userScore = it.score,
                    userStreak = it.streak,
                    userStreakState = getStreakStateUC(it.lastStreakUpdateDate),
                    isUserLoggedIn = userManager.getCurrentLoggedUser() != null,
                    isNewDailyExerciseAvailable = checkDailyExerciseAvailabilityUC(it.lastDailyExerciseDate)
                )
            }
        }
    }
}
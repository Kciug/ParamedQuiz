package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.user_management.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UserPageVM @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {
    private val _state = MutableStateFlow(UserPageState())
    val state: StateFlow<UserPageState> = _state.asStateFlow()

    fun onEvent(event: UserPageUIEvents) {
        when(event) {
            UserPageUIEvents.RefreshUserData -> getUserData()
        }
    }

    private fun getUserData() {
        userManager.getCurrentLoggedUser()?.let { userData ->
            _state.update {
                it.copy(
                    userName = userData.name,
                    userEmail = userData.email
                )
            }
        }
    }
}
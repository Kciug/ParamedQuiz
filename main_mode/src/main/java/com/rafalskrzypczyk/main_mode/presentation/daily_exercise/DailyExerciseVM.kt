package com.rafalskrzypczyk.main_mode.presentation.daily_exercise

import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.MMQuizState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class DailyExerciseVM  {
    private val _state = MutableStateFlow(MMQuizState())
    val state = _state.asStateFlow()


}
package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.utils.TimeProvider
import com.rafalskrzypczyk.core.utils.toDateOnly
import java.util.Date
import javax.inject.Inject

class CheckDailyExerciseAvailabilityUC @Inject constructor(
    private val timeProvider: TimeProvider
) {
    operator fun invoke(lastExerciseDate: Date?): Boolean {
        if(lastExerciseDate == null) return true

        val lastExerciseDateOnly = lastExerciseDate.toDateOnly()
        val today = timeProvider.now().toDateOnly()

        return lastExerciseDateOnly < today
    }
}
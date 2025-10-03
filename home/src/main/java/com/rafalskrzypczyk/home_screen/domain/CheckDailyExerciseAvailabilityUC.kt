package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.utils.toDateOnly
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class CheckDailyExerciseAvailabilityUC @Inject constructor() {
    operator fun invoke(lastExerciseDate: Date?): Boolean {
        if(lastExerciseDate == null) return true

        val lastExerciseDateOnly = lastExerciseDate.toDateOnly()
        val today = Calendar.getInstance().time.toDateOnly()

        return lastExerciseDateOnly < today
    }
}
package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.StreakManager
import com.rafalskrzypczyk.score.domain.StreakState
import java.util.Date
import javax.inject.Inject

class GetStreakStateUC @Inject constructor(
    private val streakManager: StreakManager
) {
    operator fun invoke(lastStreakUpdateDate: Date?) : StreakState {
        return if(lastStreakUpdateDate == null) StreakState.MISSED
        else streakManager.getStreakState(lastStreakUpdateDate)
    }
}
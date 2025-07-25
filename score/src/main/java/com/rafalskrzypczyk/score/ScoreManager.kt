package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScoreManager @Inject constructor(
    private val repository: ScoreRepository,
    private val userManager: UserManager,
    private val ioScope: CoroutineScope
) {
    private var score: Score = Score(0, emptyList())

    private var syncJob: Job? = null
    private val syncJobDebounce = 30000L

    private var isDirty = false

    init {
        if(userManager.getCurrentLoggedUser() != null) {
            fetchUserScore()
        } else {
            clearScore()
        }
    }

    private fun fetchUserScore() {
        ioScope.launch {
            userManager.getCurrentLoggedUser()?.let {
                repository.getUserScore(it.id).collectLatest {
                    if (it is Response.Success) {
                        score = it.data
                    }
                }
            }
        }
    }

    private suspend fun syncScore() {
        userManager.getCurrentLoggedUser()?.let {
            repository.saveUserScore(it.id, score).collectLatest { }
        }
        isDirty = false
    }

    private fun syncDebounced() {
        syncJob?.cancel()
        syncJob = ioScope.launch {
            delay(syncJobDebounce)
            syncScore()
        }
    }

    private fun clearScore() {
        score = Score(0, emptyList())
    }

    fun getScore() : Score = score

    fun updateScore(score: Score) {
        this.score = score
        isDirty = true
        syncDebounced()
    }

    fun forceSync() {
        syncJob?.cancel()
        if(isDirty){
            ioScope.launch {
                syncScore()
            }
        }
    }

    fun onUserLogIn() {
        fetchUserScore()
    }

    fun onUserLogOut() {
        forceSync()
        clearScore()
    }
}
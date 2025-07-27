package com.rafalskrzypczyk.score

import android.util.Log
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreRepository
import com.rafalskrzypczyk.score.domain.isEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScoreManager @Inject constructor(
    private val repository: ScoreRepository,
    private val ioScope: CoroutineScope
) {
    private var score: Score = Score(0, emptyList())

    private var syncJob: Job? = null
    private val syncJobDebounce = 30000L

    private var isDirty = false

    init {
        fetchUserScore()
    }

    private fun fetchUserScore() {
        ioScope.launch {
            repository.getUserScore().collectLatest {
                if(it is Response.Success) {
                    score = it.data
                    isDirty = false
                }
            }
        }
    }

    private suspend fun syncScore() {
        repository.saveUserScore(score).collectLatest {
            //TODO handle response
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
        score = Score.empty()
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

    fun onUserRegister() {
        if(!score.isEmpty()) {
            Log.d("KURWA", "SCORE IS NOT EMPTY")
            forceSync()
            repository.clearLocalScoreData()
        }
    }

    fun onUserLogIn() {
        fetchUserScore()
    }

    fun onUserLogOut() {
        forceSync()
        clearScore()
    }

    fun onUserDelete() {
        repository.deleteUserScore()
    }
}
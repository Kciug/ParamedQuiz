package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScoreManager @Inject constructor(
    private val repository: ScoreRepository,
    private val ioScope: CoroutineScope
) {
    private var score = MutableStateFlow(Score.empty())

    private val _errorFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorFlow: SharedFlow<String> = _errorFlow.asSharedFlow()

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
                    score.value = it.data
                    isDirty = false
                }
                if(it is Response.Error) _errorFlow.emit(it.error)
            }
        }
    }

    private suspend fun syncScore() {
        repository.saveUserScore(score.value).collectLatest {
            if(it is Response.Error) _errorFlow.emit(it.error)
            if(it is Response.Success) isDirty = false
        }
    }

    private fun syncDebounced() {
        syncJob?.cancel()
        syncJob = ioScope.launch {
            delay(syncJobDebounce)
            syncScore()
        }
    }

    private fun clearScore() {
        score.value = Score.empty()
    }

    fun getScoreFlow() : Flow<Score> = score

    fun getScore() : Score = score.value

    fun updateScore(score: Score) {
        this.score.value = score
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
        if(!score.value.isEmpty()) {
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
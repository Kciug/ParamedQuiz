package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScoreManager @Inject constructor(
    private val repository: ScoreRepository,
    private val userManager: UserManager,
    private val ioScope: CoroutineScope
) {
    private lateinit var score: Score

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

    private fun clearScore() {
        score = Score(0, emptyList())
    }

    fun getScore() : Score = score

    fun updateScore(score: Score) {
        this.score = score
    }

    fun saveScore() {
        ioScope.launch {
            userManager.getCurrentLoggedUser()?.let {
                repository.saveUserScore(it.id, score).collectLatest { }
            }
        }
    }

    fun onUserLogIn() {
        fetchUserScore()
    }

    fun onUserLogOut() {
        clearScore()
    }
}
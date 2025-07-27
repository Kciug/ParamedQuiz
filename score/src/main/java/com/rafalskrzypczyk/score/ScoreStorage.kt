package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.score.domain.Score

interface ScoreStorage {
    fun saveScore(score: Score)
    fun getScore(): Score
    fun clearScore()
}
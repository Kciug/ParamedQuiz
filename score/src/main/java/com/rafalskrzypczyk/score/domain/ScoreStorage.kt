package com.rafalskrzypczyk.score.domain

interface ScoreStorage {
    fun saveScore(score: Score)
    fun getScore(): Score
    fun clearScore()
}
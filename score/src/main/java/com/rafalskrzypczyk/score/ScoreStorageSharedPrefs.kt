package com.rafalskrzypczyk.score

import android.content.SharedPreferences
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesService.Companion.DEFAULT_STRING_VALUE
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesService.Companion.KEY_CURRENT_USER
import com.rafalskrzypczyk.score.domain.Score
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ScoreStorageSharedPrefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ScoreStorage {
    companion object {
        const val KEY_USER_SCORE = "user_score"
    }

    override fun saveScore(score: Score) {
        sharedPreferences.edit()
            .putString(KEY_USER_SCORE, Json.encodeToString(score))
            .apply()
    }

    override fun getScore(): Score {
        val json = sharedPreferences.getString(KEY_CURRENT_USER, DEFAULT_STRING_VALUE)
        if (json.isNullOrEmpty()) return Score.empty()
        return Json.decodeFromString<Score>(json)
    }

    override fun clearScore() {
        sharedPreferences.edit()
            .remove(KEY_USER_SCORE)
            .apply()
    }
}
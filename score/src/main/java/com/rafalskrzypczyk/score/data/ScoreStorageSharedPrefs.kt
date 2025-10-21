package com.rafalskrzypczyk.score.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesService
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreStorage
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ScoreStorageSharedPrefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ScoreStorage {
    companion object {
        const val KEY_USER_SCORE = "user_score"
    }

    override fun saveScore(score: Score) {
        sharedPreferences.edit {
            putString(KEY_USER_SCORE, Json.Default.encodeToString(score))
        }
    }

    override fun getScore(): Score {
        val json = sharedPreferences.getString(
            KEY_USER_SCORE,
            SharedPreferencesService.Companion.DEFAULT_STRING_VALUE
        )
        if (json.isNullOrEmpty()) return Score.Companion.empty()
        return Json.Default.decodeFromString<Score>(json)
    }

    override fun clearScore() {
        sharedPreferences.edit {
            remove(KEY_USER_SCORE)
        }
    }
}
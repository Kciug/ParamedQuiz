package com.rafalskrzypczyk.core.feedback

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val prefs: SharedPreferencesApi
) : FeedbackManager {

    private val vibrator: Vibrator? by lazy { resolveVibrator() }

    private val soundResources: Map<FeedbackEvent, Int> = mapOf(
        FeedbackEvent.ANSWER_CORRECT to R.raw.fb_correct,
        FeedbackEvent.ANSWER_WRONG to R.raw.fb_wrong,
        FeedbackEvent.QUIZ_COMPLETED to R.raw.fb_complete,
        FeedbackEvent.NEW_RECORD to R.raw.fb_record
    )

    private val soundPool: SoundPool by lazy {
        SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    private val loadedSounds: Map<FeedbackEvent, Int> by lazy {
        soundResources.mapValues { soundPool.load(context, it.value, 1) }
    }

    init {
        loadedSounds
    }

    override fun perform(event: FeedbackEvent) {
        if (prefs.isHapticEnabled()) vibrate(event)
        if (prefs.isSoundEnabled()) playSound(event)
    }

    private fun vibrate(event: FeedbackEvent) {
        val activeVibrator = vibrator ?: return
        if (!activeVibrator.hasVibrator()) return

        val timings = timingsFor(event)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = if (timings.size == 1) {
                VibrationEffect.createOneShot(timings.first(), VibrationEffect.DEFAULT_AMPLITUDE)
            } else {
                VibrationEffect.createWaveform(timings, NO_REPEAT)
            }
            activeVibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            if (timings.size == 1) activeVibrator.vibrate(timings.first())
            else activeVibrator.vibrate(timings, NO_REPEAT)
        }
    }

    private fun playSound(event: FeedbackEvent) {
        val soundId = loadedSounds[event] ?: return
        soundPool.play(soundId, VOLUME, VOLUME, PRIORITY, NO_LOOP, PLAYBACK_RATE)
    }

    private fun timingsFor(event: FeedbackEvent): LongArray = when (event) {
        FeedbackEvent.CLICK -> longArrayOf(20)
        FeedbackEvent.ANSWER_CORRECT -> longArrayOf(0, 25, 60, 25)
        FeedbackEvent.ANSWER_WRONG -> longArrayOf(0, 120)
        FeedbackEvent.QUIZ_COMPLETED -> longArrayOf(0, 30, 50, 30, 50, 60)
        FeedbackEvent.NEW_RECORD -> longArrayOf(0, 40, 40, 40, 40, 90)
        FeedbackEvent.ERROR -> longArrayOf(0, 80, 60, 80)
    }

    private fun resolveVibrator(): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    companion object {
        private const val MAX_STREAMS = 4
        private const val NO_REPEAT = -1
        private const val NO_LOOP = 0
        private const val PRIORITY = 1
        private const val VOLUME = 1f
        private const val PLAYBACK_RATE = 1f
    }
}

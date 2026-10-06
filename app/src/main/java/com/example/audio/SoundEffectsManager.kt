package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundEffectsManager {

    private const val SAMPLE_RATE = 44100

    /**
     * Plays a futuristic sci-fi power-up sound effect when VPN connects.
     */
    fun playConnectSound() {
        CoroutineScope(Dispatchers.Default).launch {
            val durationSeconds = 0.48
            val numSamples = (durationSeconds * SAMPLE_RATE).toInt()
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = t / durationSeconds
                // Rising cyber frequency sweep: 440 Hz up to 1480 Hz with harmonic overtone
                val baseFreq = 400.0 + (1080.0 * progress * progress)
                val harmonicFreq = baseFreq * 1.5

                val env = when {
                    progress < 0.1 -> progress / 0.1 // quick attack
                    progress > 0.8 -> (1.0 - progress) / 0.2 // fade out
                    else -> 1.0
                }

                val wave1 = sin(2.0 * PI * baseFreq * t)
                val wave2 = 0.4 * sin(2.0 * PI * harmonicFreq * t)
                val combined = (wave1 + wave2) * env * 0.7

                sample[i] = (combined * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            playPcm(sample)
        }
    }

    /**
     * Plays a futuristic sci-fi power-down sound effect when VPN disconnects.
     */
    fun playDisconnectSound() {
        CoroutineScope(Dispatchers.Default).launch {
            val durationSeconds = 0.45
            val numSamples = (durationSeconds * SAMPLE_RATE).toInt()
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = t / durationSeconds
                // Descending cyber frequency sweep: 1300 Hz down to 220 Hz
                val baseFreq = 1300.0 - (1080.0 * progress)
                val subFreq = baseFreq * 0.5

                val env = when {
                    progress < 0.08 -> progress / 0.08
                    progress > 0.75 -> (1.0 - progress) / 0.25
                    else -> 1.0
                }

                val wave1 = sin(2.0 * PI * baseFreq * t)
                val wave2 = 0.35 * sin(2.0 * PI * subFreq * t)
                val combined = (wave1 + wave2) * env * 0.65

                sample[i] = (combined * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            playPcm(sample)
        }
    }

    private fun playPcm(samples: ShortArray) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufferSize, samples.size * 2)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()

            // Release after playing
            Thread.sleep((samples.size * 1000L / SAMPLE_RATE) + 100)
            audioTrack.release()
        } catch (_: Exception) {
            // Silently handle if audio focus or device is unavailable
        }
    }
}

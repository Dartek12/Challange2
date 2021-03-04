package com.example.androiddevchallenge

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@Stable
sealed class AppState

data class SettingsState(val sequence: String = ""): AppState() {
    val isNonZero: Boolean
        get() = sequence.isNotEmpty() && asSeconds() > 0

    fun tryPushDigit(digit: Char): SettingsState {
        if(sequence.length >= 6 || (sequence.isEmpty() && digit == '0')) {
            return this
        }
        return copy(sequence = sequence.plus(digit))
    }

    fun tryPopDigit(): SettingsState {
        if(sequence.isEmpty()) {
            return this
        }
        return copy(sequence = sequence.substring(0, sequence.lastIndex))
    }

    fun asSeconds(): Int {
        val padded = sequence.padStart(6, '0')
        val hours = padded.substring(0, 2).toInt()
        val minutes = padded.substring(2, 4).toInt()
        val seconds = padded.substring(4, 6).toInt()
        return hours * 3600 + minutes * 60 + seconds
    }
}

data class CountdownState(val paused: Boolean, val current: Int, val target: Int) : AppState() {
    val progress: Float = current.toFloat() / target.toFloat() * 100f

    fun tick(): CountdownState = copy(current = (current + 1).coerceAtMost(target))

    fun pause(): CountdownState = copy(paused = true)

    fun resume(): CountdownState = copy(paused = false)
}
package com.practicum.playlistmaker.player.presentation

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String,
    val isPlaying: Boolean = false,
) {

    class Default : PlayerState(false, "00:00")

    class Prepared : PlayerState(true, "00:00")

    class Playing(progress: String) : PlayerState(true, progress, true)

    class Paused(progress: String) : PlayerState(true, progress, false)

}
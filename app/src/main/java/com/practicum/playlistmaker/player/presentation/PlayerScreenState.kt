package com.practicum.playlistmaker.player.presentation

data class PlayerScreenState(
    val isPlayButtonEnabled: Boolean = false,
    val progress: String = "00:00",
    val playerState: PlayerState = PlayerState.DEFAULT,
    val isFavorite: Boolean,
)
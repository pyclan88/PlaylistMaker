package com.practicum.playlistmaker.player.ui.model

data class PlayerState(
    var isPlaying: Boolean =false,
    var prepared: Boolean = false,
    var progress: Int = 0,
)

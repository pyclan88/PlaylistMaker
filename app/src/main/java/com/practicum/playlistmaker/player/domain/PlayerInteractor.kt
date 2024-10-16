package com.practicum.playlistmaker.player.domain

interface PlayerInteractor {
    fun preparePlayer(
        trackUrl: String?,
        onPrepared: () -> Unit,
        onComplete: () -> Unit
    )

    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun resetPlayer()
}
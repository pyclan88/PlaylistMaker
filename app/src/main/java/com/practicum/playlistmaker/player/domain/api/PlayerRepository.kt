package com.practicum.playlistmaker.player.domain.api

interface PlayerRepository {

    fun preparePlayer(
        trackUrl: String?,
        onPrepared: () -> Unit,
        onComplete: () -> Unit
    )

    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun resetPlayer()
    fun getCurrentPosition(): Int

}
package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.Track

interface PlayerRepository {

    fun preparePlayer(trackUrl: String, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun resetPlayer()
    fun getCurrentPosition(): Int
    fun getTrack(): Track

}
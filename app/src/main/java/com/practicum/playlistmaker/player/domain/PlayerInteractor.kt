package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.model.Track

interface PlayerInteractor {
    fun preparePlayer(trackUrl: String, onComplete: () -> Unit)

    fun startPlayer(statusObserver: StatusObserver)
    fun pausePlayer()
    fun getCurrentPosition(): Int

    fun resetPlayer()

    fun getTrack(): Track

    interface StatusObserver {
        fun onStop()
        fun onPlay()
    }

}
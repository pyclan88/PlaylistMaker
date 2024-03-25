package com.practicum.playlistmaker.domain.api

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.models.Track

interface PlayerRepository {
    fun preparePlayer(preparedCallback: () -> Unit, completionCallback: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun getPlayer(): MediaPlayer
    fun getTrack(): Track
}
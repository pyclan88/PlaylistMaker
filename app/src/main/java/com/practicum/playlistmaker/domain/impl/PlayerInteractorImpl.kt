package com.practicum.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(preparedCallback: () -> Unit, completionCallback: () -> Unit) {
        repository.preparePlayer(
            preparedCallback = {
                preparedCallback.invoke()
            },
            completionCallback = {
                completionCallback.invoke()
            }
        )
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun getPlayer(): MediaPlayer {
        return repository.getPlayer()
    }

    override fun getTrack(): Track {
        return repository.getTrack()
    }
}
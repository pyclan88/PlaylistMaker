package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.Track

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    private var statusObserver: PlayerInteractor.StatusObserver? = null

    override fun preparePlayer(
        trackUrl: String,
        onComplete: () -> Unit
    ) {
        repository.preparePlayer(
            trackUrl = trackUrl,
            onComplete = { onComplete.invoke() }
        )
    }

    override fun startPlayer(statusObserver: PlayerInteractor.StatusObserver) {
        this.statusObserver = statusObserver
        statusObserver.onPlay()
        repository.startPlayer()
    }

    override fun pausePlayer() {
        statusObserver?.onStop()
        repository.pausePlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun getTrack(): Track {
        return repository.getTrack()
    }
}
package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.TrackInteractor
import org.koin.core.component.KoinComponent

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository, KoinComponent {

    private val trackInteractor: TrackInteractor = getKoin().get()
    private val currentTrack = trackInteractor.loadTrack()

    override fun preparePlayer(
        trackUrl: String?,
        onPrepared: () -> Unit,
        onComplete: () -> Unit
    ) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onComplete.invoke()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun resetPlayer() {
        mediaPlayer.reset()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getTrack(): Track {
        return currentTrack
    }

}
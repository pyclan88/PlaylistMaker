package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.Track
import org.koin.core.component.KoinComponent

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

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

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun resetPlayer() {
        mediaPlayer.reset()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}
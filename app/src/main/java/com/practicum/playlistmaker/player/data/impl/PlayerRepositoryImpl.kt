package com.practicum.playlistmaker.player.data.impl

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.Track

class PlayerRepositoryImpl(val context: Context) : PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private val currentTrack = Creator.provideTrackInteractor(context).loadTrack()

    override fun preparePlayer(
        trackUrl: String,
        onComplete: () -> Unit
    ) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
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

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getTrack(): Track {
        return currentTrack
    }
}
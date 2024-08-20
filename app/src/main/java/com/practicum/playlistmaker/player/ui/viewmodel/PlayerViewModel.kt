package com.practicum.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.ui.model.PlayStatus
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.Track


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val previewUrl = playerInteractor.getTrack().previewUrl

    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private val progressLiveData = MutableLiveData<Int>()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val progressUpdateRunnable = object : Runnable {
        override fun run() {
            progressLiveData.postValue(playerInteractor.getCurrentPosition())
            mainThreadHandler.postDelayed(this, PLAYER_REQUEST_TOKEN, DELAY_MILLIS)
        }
    }

    init {

        if (previewUrl != null) {

            playerInteractor.preparePlayer(
                trackUrl = previewUrl,
                onComplete = {
                    mainThreadHandler.removeCallbacks(progressUpdateRunnable)
                    playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
                    progressLiveData.postValue(0)
                }
            )

        }

    }

    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    fun getProgressLiveData(): LiveData<Int> = progressLiveData

    fun play() {
        playerInteractor.startPlayer(
            statusObserver = object : PlayerInteractor.StatusObserver {
                override fun onStop() {
                    playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
                    mainThreadHandler.removeCallbacks(progressUpdateRunnable)
                }

                override fun onPlay() {
                    playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = true))
                    mainThreadHandler.post(progressUpdateRunnable)
                }

            }
        )
    }

    fun pause() {
        playerInteractor.pausePlayer()
    }

    fun provideCurrentTrack(): Track {
        return playerInteractor.getTrack()
    }

    fun isPreviewUrlValid(): Boolean {
        return (previewUrl != null)
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(isPlaying = false)
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacksAndMessages(PLAYER_REQUEST_TOKEN)
        playerInteractor.resetPlayer()
    }

    companion object {
        private const val DELAY_MILLIS = 300L
        private val PLAYER_REQUEST_TOKEN = Any()
    }

}
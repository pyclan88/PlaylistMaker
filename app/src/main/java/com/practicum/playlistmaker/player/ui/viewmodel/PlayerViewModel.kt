package com.practicum.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.ui.model.PlayerState
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.Track


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val previewUrl = playerInteractor.getTrack().previewUrl

    private val playerStateLiveData = MutableLiveData<PlayerState>()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val progressUpdateRunnable = object : Runnable {
        override fun run() {
            val currentState = playerStateLiveData.value ?: PlayerState()
            playerStateLiveData.postValue(
                currentState.copy(progress = playerInteractor.getCurrentPosition())
            )
            mainThreadHandler.postDelayed(this, PLAYER_REQUEST_TOKEN, DELAY_MILLIS)
        }
    }

    init {

        if (previewUrl != null) {

            playerInteractor.preparePlayer(
                trackUrl = previewUrl,
                onPrepared = {
                    playerStateLiveData.postValue(PlayerState(false, prepared = true, 0))
                },
                onComplete = {
                    mainThreadHandler.removeCallbacks(progressUpdateRunnable)
                    playerStateLiveData.postValue(
                        getCurrentPlayStatus().copy(
                            isPlaying = false,
                            progress = 0
                        )
                    )
                }
            )

        }

    }

    fun getPlayStatusLiveData(): LiveData<PlayerState> = playerStateLiveData

    fun play() {
        playerInteractor.startPlayer(
            statusObserver = object : PlayerInteractor.StatusObserver {
                override fun onStop() {
                    playerStateLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
                    mainThreadHandler.removeCallbacks(progressUpdateRunnable)
                }

                override fun onPlay() {
                    playerStateLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = true))
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

    private fun getCurrentPlayStatus(): PlayerState {
        return playerStateLiveData.value ?: PlayerState(isPlaying = false, false, 0)
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
package com.practicum.playlistmaker.player.ui.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.ui.model.PlayStatus
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.Track


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    companion object {
        private const val DELAY_MILLIS = 300L
        private val PLAYER_REQUEST_TOKEN = Any()

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    Creator.providePlayerInteractor(context)
                )
            }
        }
    }

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
        playerInteractor.preparePlayer(
            trackUrl = playerInteractor.getTrack().previewUrl,
            onComplete = {
                mainThreadHandler.removeCallbacks(progressUpdateRunnable)
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
                progressLiveData.postValue(0)
            }
        )
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

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(isPlaying = false)
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacksAndMessages(PLAYER_REQUEST_TOKEN)
        playerInteractor.releasePlayer()
    }
}
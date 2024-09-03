package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.presentation.PlayerState.Prepared
import com.practicum.playlistmaker.player.presentation.PlayerState.Playing
import com.practicum.playlistmaker.player.presentation.PlayerState.Paused
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.util.DateTimeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val previewUrl = playerInteractor.getTrack().previewUrl

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    init {
        initMediaPlayer()
    }

    override fun onCleared() {
        playerInteractor.resetPlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is Playing -> pausePlayer()
            is Prepared, is Paused -> startPlayer()
            else -> {}
        }
    }

    private fun initMediaPlayer() {
        playerInteractor.preparePlayer(
            trackUrl = previewUrl,
            onPrepared = {
                playerState.postValue(Prepared())
            },
            onComplete = {
                timerJob?.cancel()
                playerState.postValue(Prepared())
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerState.postValue(Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        playerState.postValue(Paused(getCurrentPlayerPosition()))
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY_MILLIS)
                playerState.postValue(Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return DateTimeUtil.formatTime(playerInteractor.getCurrentPosition())
    }

    fun provideCurrentTrack(): Track {
        return playerInteractor.getTrack()
    }

    fun isPreviewUrlNotValid(): Boolean {
        return (previewUrl == null)
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }

}
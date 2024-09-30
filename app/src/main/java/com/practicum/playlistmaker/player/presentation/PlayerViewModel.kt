package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.presentation.PlayerState.PREPARED
import com.practicum.playlistmaker.player.presentation.PlayerState.PLAYING
import com.practicum.playlistmaker.player.presentation.PlayerState.PAUSED
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.utils.DateTimeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private var track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
) : ViewModel() {

    private var timerJob: Job? = null

    private val screenStateLiveData =
        MutableLiveData(PlayerScreenState(isFavorite = track.isFavorite))
    fun observePlayerState(): LiveData<PlayerScreenState> = screenStateLiveData

    init {
        initMediaPlayer()
    }

    override fun onCleared() {
        playerInteractor.resetPlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayClicked() {
        when (screenStateLiveData.value?.playerState) {
            PLAYING -> pausePlayer()
            PREPARED, PAUSED -> startPlayer()
            else -> {}
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val newFavoriteState = !track.isFavorite
            track.isFavorite = newFavoriteState
            if (newFavoriteState) {
                favoriteInteractor.addTrackToFavorites(track)
            } else {
                favoriteInteractor.removeTrackFromFavorites(track)
            }
            updatePlayerScreenState(isFavorite = newFavoriteState)
        }
    }

    fun provideCurrentTrack(): Track {
        return track
    }

    fun isPreviewUrlNotValid(): Boolean {
        return (track.previewUrl == null)
    }

    private fun initMediaPlayer() {
        playerInteractor.preparePlayer(
            trackUrl = track.previewUrl,
            onPrepared = {
                updatePlayerScreenState(isPlayButtonEnabled = true, playerState = PREPARED)
            },
            onComplete = {
                timerJob?.cancel()
                updatePlayerScreenState(progress = INITIAL_PROGRESS, playerState = PREPARED)
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        updatePlayerScreenState(playerState = PLAYING)
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        updatePlayerScreenState(playerState = PAUSED)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY_MILLIS)
                updatePlayerScreenState(progress = getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return DateTimeUtil.formatTime(playerInteractor.getCurrentPosition())
    }

    private fun updatePlayerScreenState(
        isPlayButtonEnabled: Boolean? = null,
        progress: String? = null,
        playerState: PlayerState? = null,
        isFavorite: Boolean? = null,
    ) {
        val currentState = screenStateLiveData.value
        val updatedState = currentState?.copy(
            isPlayButtonEnabled = isPlayButtonEnabled ?: currentState.isPlayButtonEnabled,
            progress = progress ?: currentState.progress,
            playerState = playerState ?: currentState.playerState,
            isFavorite = isFavorite ?: currentState.isFavorite
        )
        screenStateLiveData.value = updatedState
    }

    companion object {
        private const val DELAY_MILLIS = 300L
        private const val INITIAL_PROGRESS = "00:00"
    }

}
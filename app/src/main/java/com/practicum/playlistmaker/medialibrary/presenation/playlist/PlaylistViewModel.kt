package com.practicum.playlistmaker.medialibrary.presenation.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.presenation.newplaylist.PlaylistScreenState
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    init {
        loadPlaylists()
    }

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.playlists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(loadedPlaylists: List<Playlist>?) {
        val playlists = ArrayList<Playlist>()
        if (loadedPlaylists != null) {
            playlists.addAll(loadedPlaylists)
        }
        if (playlists.isNotEmpty()) {
            setState(PlaylistScreenState.Content(playlists))
        } else {
            setState(PlaylistScreenState.Empty)
        }
    }

    private fun setState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

}
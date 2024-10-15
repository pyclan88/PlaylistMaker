package com.practicum.playlistmaker.medialibrary.presenation.favoritetracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteInteractor: FavoriteInteractor,
) : ViewModel() {

    init {
        loadFavoriteTracks()
    }

    private val stateLiveData = MutableLiveData<FavoriteScreenState>()
    fun observerState(): LiveData<FavoriteScreenState> = stateLiveData

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteInteractor.favoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(loadedTracks: List<Track>?) {
        val tracks = ArrayList<Track>()
        if (loadedTracks != null) {
            tracks.addAll(loadedTracks)
        }
        if (tracks.isNotEmpty()) {
            setState(FavoriteScreenState.Content(tracks))
        } else {
            setState(FavoriteScreenState.Empty)
        }
    }

    private fun setState(state: FavoriteScreenState) {
        stateLiveData.postValue(state)
    }

}
package com.practicum.playlistmaker.medialibrary.presentation.singleplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class SinglePlaylistViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val gson: Gson,
) : ViewModel() {

    private lateinit var playlist: Playlist
    private lateinit var idList: MutableList<Int>

    private val playlistStateLiveData = MutableLiveData<Playlist>()
    private val trackListStateLiveData = MutableLiveData<List<Track>>()

    private val combinedLiveData = MediatorLiveData<Pair<Playlist?, List<Track>?>>()

    init {
        loadPlaylist()

        combinedLiveData.addSource(playlistStateLiveData) { playlist ->
            val trackList = trackListStateLiveData.value
            combinedLiveData.value = Pair(playlist, trackList)
        }
    }

    fun observeCombinedState(): LiveData<Pair<Playlist?, List<Track>?>> = combinedLiveData

    fun deleteTrack(track: Track) {
        val idListJson = updateIdList(track)
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlist, idListJson, track.trackId)
        }
    }

    fun deletePlaylist(playlist: Playlist?) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist!!)
        }
    }

    private fun updateIdList(track: Track): String {
        idList.remove(track.trackId)
        val idListJson = gson.toJson(idList)
        return idListJson
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .collect { playlistFromBd ->
                    playlist = playlistFromBd
                    loadTracks(playlistFromBd)
                }
        }
    }

    private fun loadTracks(playlist: Playlist) {
        val idListString = playlist.idList
        val idListInt: List<Int> = gson.fromJson(idListString, Array<Int>::class.java).toList()
        idList = idListInt.toMutableList()

        viewModelScope.launch {
            playlistInteractor.getTracksByIds(idListInt)
                .collect { tracks ->
                    trackListStateLiveData.postValue(tracks)
                    playlistStateLiveData.postValue(playlist)
                }
        }
        idList = idListInt.toMutableList()
    }

}
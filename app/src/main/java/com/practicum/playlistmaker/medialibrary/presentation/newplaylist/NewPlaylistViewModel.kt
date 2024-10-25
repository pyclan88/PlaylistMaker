package com.practicum.playlistmaker.medialibrary.presentation.newplaylist

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.ImageInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor,
    application: Application,
) : AndroidViewModel(application) {

    init {
        loadPlaylist()
    }

    private val allPlaylistNames = mutableListOf<String>()

    private val playlistStateLiveData = MutableLiveData<Playlist>()
    fun observePlaylistState(): LiveData<Playlist> {
        return playlistStateLiveData
    }

    fun loadPlaylist() {
        if (playlistId.toInt() != -1) {
            viewModelScope.launch {
                playlistInteractor.getPlaylistById(playlistId)
                    .collect { playlistFromBd ->
                        playlistStateLiveData.postValue(playlistFromBd)
                    }
            }
        }
    }

    fun loadNames() {
        viewModelScope.launch {
            playlistInteractor.getAllNames()
                .collect { names ->
                    allPlaylistNames.addAll(names)
                }
        }
    }

    fun getAllPlaylistNames(): List<String> {
        return allPlaylistNames
    }

    fun createPlaylist(coverPath: String?, name: String, description: String?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(coverPath, name, description)
        }
    }

    fun updatePlaylist(
        playlist: Playlist?,
        coverToPlaylist: String?,
        nameToPlaylist: String,
        descriptionToPlaylist: String?
    ) {
        val editePlaylist = playlist!!.copy(
            coverPath = coverToPlaylist,
            name = nameToPlaylist,
            description = descriptionToPlaylist
        )
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(editePlaylist)
        }
    }

    fun saveImageToPrivateStorage(uri: Uri): Uri {
        val orderNumberOfPlaylist = allPlaylistNames.size + 1
        return imageInteractor.saveImage(uri, orderNumberOfPlaylist)
    }

}
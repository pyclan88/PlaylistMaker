package com.practicum.playlistmaker.medialibrary.presentation.newplaylist

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.ImageInteractor
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor,
    application: Application,
) : AndroidViewModel(application) {

    private val allPlaylistNames = mutableListOf<String>()

    fun loadNames() {
        viewModelScope.launch {
            playlistInteractor.playlistNames()
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

    fun saveImageToPrivateStorage(uri: Uri): Uri {
        val orderNumberOfPlaylist = allPlaylistNames.size + 1
        return imageInteractor.saveImage(uri, orderNumberOfPlaylist)
    }

}
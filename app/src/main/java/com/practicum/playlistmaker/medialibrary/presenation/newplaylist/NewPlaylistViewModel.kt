package com.practicum.playlistmaker.medialibrary.presenation.newplaylist

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val application: Application,
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
        val playlist = Playlist(
            coverPath = coverPath,
            name = name,
            description = description
        )
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath =
            File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Playlist_covers")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val orderNumberOfPlaylist = allPlaylistNames.size + 1
        val currentSystemTime = System.currentTimeMillis()

        val fileName = "${orderNumberOfPlaylist}_$currentSystemTime"
        val fileCover = File(filePath, fileName)

        application.contentResolver.openInputStream(uri).use { inputStream ->
            saveImageToStorage(fileCover, inputStream!!)
        }

        return Uri.fromFile(fileCover)
    }

    private fun saveImageToStorage(fileCover: File, inputStream: InputStream) {
        FileOutputStream(fileCover).use { outputStream ->
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        }
    }

}
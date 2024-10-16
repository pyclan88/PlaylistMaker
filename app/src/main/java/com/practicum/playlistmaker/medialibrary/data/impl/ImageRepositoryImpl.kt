package com.practicum.playlistmaker.medialibrary.data.impl

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.medialibrary.domain.api.ImageRepository
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageRepositoryImpl(private val application: Application) : ImageRepository {

    override fun saveImageToPrivateStorage(uri: Uri, playlistNumber: Int): Uri {
        val filePath =
            File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Playlist_covers")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val orderNumberOfPlaylist = playlistNumber
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
package com.practicum.playlistmaker.medialibrary.domain.api

import android.net.Uri

interface ImageRepository {
    fun saveImageToPrivateStorage(uri: Uri, playlistNumber: Int): Uri
}
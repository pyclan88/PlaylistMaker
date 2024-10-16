package com.practicum.playlistmaker.medialibrary.domain

import android.net.Uri

interface ImageInteractor {
    fun saveImage(uri: Uri, playlistNumber: Int): Uri
}
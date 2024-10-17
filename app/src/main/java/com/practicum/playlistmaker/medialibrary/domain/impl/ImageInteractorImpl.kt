package com.practicum.playlistmaker.medialibrary.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.medialibrary.domain.ImageInteractor
import com.practicum.playlistmaker.medialibrary.domain.api.ImageRepository

class ImageInteractorImpl(private val imageRepository: ImageRepository) : ImageInteractor {
    override fun saveImage(uri: Uri, playlistNumber: Int): Uri {
        return imageRepository.saveImageToPrivateStorage(
            uri = uri,
            playlistNumber = playlistNumber
        )
    }
}
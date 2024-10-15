package com.practicum.playlistmaker.medialibrary.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val idList: String = "[]",
    val count: Int = 0,
    val lastModifiedAt: Long = 0,
)

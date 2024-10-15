package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

data class PlayerScreenState(
    val isPlayButtonEnabled: Boolean = false,
    val progress: String = "00:00",
    val playerState: PlayerState = PlayerState.DEFAULT,
    val isFavorite: Boolean,
    val playlists: List<Playlist>? = null,
)
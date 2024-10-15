package com.practicum.playlistmaker.medialibrary.presenation.newplaylist

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

interface PlaylistScreenState {

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistScreenState

    object Empty : PlaylistScreenState

}
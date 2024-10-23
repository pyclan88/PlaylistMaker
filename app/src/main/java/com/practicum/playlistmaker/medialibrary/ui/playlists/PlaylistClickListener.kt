package com.practicum.playlistmaker.medialibrary.ui.playlists

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

fun interface PlaylistClickListener {
    fun onPlaylistClick(playlist: Playlist)
}
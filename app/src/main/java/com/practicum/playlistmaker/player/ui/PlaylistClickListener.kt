package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

fun interface PlaylistClickListener {
    fun onPlaylistClick(playlist: Playlist)
}
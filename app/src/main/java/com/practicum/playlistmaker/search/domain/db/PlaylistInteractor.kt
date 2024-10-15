package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist, idList: String)
    suspend fun playlists(): Flow<List<Playlist>>
    suspend fun playlistNames(): Flow<List<String>>
    suspend fun addTrackToPlaylistTrack(track: Track)
}
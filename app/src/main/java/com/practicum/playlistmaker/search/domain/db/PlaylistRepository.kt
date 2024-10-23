package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlist: Playlist, idList: String)
    suspend fun updatePlaylistAfterTrackRemoval(playlist: Playlist, idList: String, idTrack: Int)
    suspend fun playlists(): Flow<List<Playlist>>
    suspend fun playlistNames(): Flow<List<String>>
    suspend fun getPlaylistById(id: Long): Flow<Playlist>
    suspend fun addTrackToPlaylistTrack(track: Track)
    suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}
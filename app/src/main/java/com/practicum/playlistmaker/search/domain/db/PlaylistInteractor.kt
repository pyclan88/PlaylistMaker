package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlist: Playlist, idList: String)
    suspend fun removeTrackFromPlaylist(playlist: Playlist, idList: String, trackId: Int)
    suspend fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getAllNames(): Flow<List<String>>
    suspend fun createPlaylist(coverPath: String?, name: String, description: String?)
    suspend fun getPlaylistById(id: Long): Flow<Playlist>
    suspend fun addTrackToPlaylistTrack(track: Track)
    suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}
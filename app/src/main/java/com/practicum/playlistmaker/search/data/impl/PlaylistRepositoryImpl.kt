package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.converter.PlaylistDbConverter
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.db.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        val updatedPlaylistEntity = playlistEntity.copy(lastModifiedAt = System.currentTimeMillis())
        appDatabase.playlistDao().insertPlaylist(updatedPlaylistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist, idList: String) {
        val playlistEntity = playlistDbConverter.map(playlist)
        val updatedPlaylistEntity = playlistEntity.copy(
            lastModifiedAt = System.currentTimeMillis(),
            count = playlist.count + 1,
            idList = idList
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)
    }

    override suspend fun playlists(): Flow<List<Playlist>> {
        val playlistEntitiesFlow = appDatabase.playlistDao().getAllPlaylists()
        val playlistsFlow = playlistEntitiesFlow.map { playlistEntities ->
            playlistEntities.map { playlistEntity ->
                playlistDbConverter.map(playlistEntity)
            }
        }
        return playlistsFlow
    }

    override suspend fun playlistNames(): Flow<List<String>> = flow {
        val names: List<String> = appDatabase.playlistDao().getAllNames()
        emit(names)
    }

    override suspend fun addTrackToPlaylistTrack(track: Track) {
        val playlistTrackEntity = playlistDbConverter.map(track)
        appDatabase.playlistTrackDao().insertTrack(playlistTrackEntity)
    }
}
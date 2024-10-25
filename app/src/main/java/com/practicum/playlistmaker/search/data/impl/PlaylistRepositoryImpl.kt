package com.practicum.playlistmaker.search.data.impl

import android.util.Log
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

    override suspend fun addTrackToPlaylist(playlist: Playlist, idList: String) {
        val playlistEntity = playlistDbConverter.map(playlist)
        val updatedPlaylistEntity = playlistEntity.copy(
            lastModifiedAt = System.currentTimeMillis(),
            count = playlist.count + 1,
            idList = idList
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)
    }

     override suspend fun updatePlaylistAfterTrackRemoval(playlist: Playlist, idList: String, trackId: Int) {
        val playlistEntity = playlistDbConverter.map(playlist)
        val updatedPlaylistEntity = playlistEntity.copy(
            lastModifiedAt = System.currentTimeMillis(),
            count = playlist.count - 1,
            idList = idList
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)

         deleteTrackIfUnused(trackId)
    }

    private suspend fun deleteTrackIfUnused(trackId: Int) {
        val count = appDatabase.playlistDao().countPlaylistsContainingTrack(trackId)
        if (count == 0) {
            appDatabase.playlistTrackDao().deleteTrack(trackId)
        }
    }

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
        val playlistEntitiesFlow = appDatabase.playlistDao().getAllPlaylists()
        val playlistsFlow = playlistEntitiesFlow.map { playlistEntities ->
            playlistEntities.map { playlistEntity ->
                playlistDbConverter.map(playlistEntity)
            }
        }
        return playlistsFlow
    }

    override suspend fun getAllNames(): Flow<List<String>> = flow {
        val names: List<String> = appDatabase.playlistDao().getAllNames()
        emit(names)
    }

    override suspend fun getPlaylistById(id: Long): Flow<Playlist> {
        val playlistEntityFlow = appDatabase.playlistDao().getPlaylistById(id)
        val playlistFlow = playlistEntityFlow.map { playlistEntity ->
            playlistDbConverter.map(playlistEntity)
        }
        return playlistFlow
    }

    override suspend fun addTrackToPlaylistTrack(track: Track) {
        val playlistTrackEntity = playlistDbConverter.map(track)
        val updatedPlaylistTrackEntity = playlistTrackEntity.copy(addedAt = System.currentTimeMillis())
        appDatabase.playlistTrackDao().insertTrack(updatedPlaylistTrackEntity)
    }

    override suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>> {
        val playlistTrackEntitiesFlow = appDatabase.playlistTrackDao().getTracksByIds(ids)
        val tracksFlow = playlistTrackEntitiesFlow.map { playlistTrackEntities ->
            playlistTrackEntities.map { playlistTrackEntity ->
                playlistDbConverter.map(playlistTrackEntity)
            }
        }
        return tracksFlow
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlist.id)
        val idList = playlist.idList
            .replace("[", "")
            .replace("]", "")
            .split(",")

        val ids = idList.map { it.trim().toInt() }
        ids.forEach { deleteTrackIfUnused(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        Log.d("TEST", playlist.coverPath.toString())
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

}
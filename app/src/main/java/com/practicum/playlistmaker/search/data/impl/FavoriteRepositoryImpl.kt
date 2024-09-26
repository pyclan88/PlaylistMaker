package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.converter.TrackDbConverter
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val favoriteTrackEntity = trackDbConverter.map(track, FavoriteTrackEntity::class)
        val updatedFavoriteTrackEntity = favoriteTrackEntity.copy(addedAt = System.currentTimeMillis())
        appDatabase.favoriteTrackDao().insertTrack(updatedFavoriteTrackEntity)
    }

    override suspend fun getTrack(trackId: Int): Flow<Track> = flow {
        val favoriteTrackEntity = appDatabase.favoriteTrackDao().getTrackById(trackId)
        val track = trackDbConverter.map(favoriteTrackEntity)
        emit(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val favoriteTrackEntity = trackDbConverter.map(track, FavoriteTrackEntity::class)
        appDatabase.favoriteTrackDao().deleteTrack(favoriteTrackEntity)
    }

    override suspend fun favoriteTracks(): Flow<List<Track>> = flow {
        val favoriteTrackEntities = appDatabase.favoriteTrackDao().getAllTracks()
        val tracks = favoriteTrackEntities.map { track -> trackDbConverter.map(track) }
        emit(tracks)
    }

    override suspend fun getAllIds(): Flow<List<Int>> = flow {
        val favoriteIds = appDatabase.favoriteTrackDao().getAllIds()
        emit(favoriteIds)
    }

}
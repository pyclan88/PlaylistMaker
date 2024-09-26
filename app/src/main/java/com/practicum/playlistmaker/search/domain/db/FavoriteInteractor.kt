package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun getTrack(trackId: Int): Flow<Track>
    suspend fun removeTrackFromFavorites(track: Track)
    suspend fun favoriteTracks(): Flow<List<Track>>
    suspend fun getAllIds(): Flow<List<Int>>
}
package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    suspend fun favoriteTracks(): Flow<List<Track>>
}
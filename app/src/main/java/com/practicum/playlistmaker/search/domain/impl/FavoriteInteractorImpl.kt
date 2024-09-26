package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository,
) : FavoriteInteractor {

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteRepository.addTrackToFavorites(track)
    }

    override suspend fun getTrack(trackId: Int): Flow<Track> {
        return favoriteRepository.getTrack(trackId)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteRepository.removeTrackFromFavorites(track)
    }

    override suspend fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.favoriteTracks()
    }

    override suspend fun getAllIds(): Flow<List<Int>> {
        return favoriteRepository.getAllIds()
    }

}
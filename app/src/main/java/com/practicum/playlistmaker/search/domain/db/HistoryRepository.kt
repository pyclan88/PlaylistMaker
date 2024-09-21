package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun saveTrackToHistory(track: Track)
    suspend fun historyTracks(): Flow<List<Track>>
    suspend fun clearHistory()
}
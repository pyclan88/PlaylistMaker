package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryInteractor {
    suspend fun addTrackToHistory(track: Track)
    suspend fun clearHistory()
    suspend fun historyTracks(): Flow<List<Track>>
}
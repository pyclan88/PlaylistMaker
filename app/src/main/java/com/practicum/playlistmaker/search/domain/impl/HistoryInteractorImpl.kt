package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.HistoryInteractor
import com.practicum.playlistmaker.search.domain.db.HistoryRepository
import kotlinx.coroutines.flow.Flow

class HistoryInteractorImpl(
    private val historyRepository: HistoryRepository,
) : HistoryInteractor {

    override suspend fun addTrackToHistory(track: Track) {
        historyRepository.addTrackToHistory(track)
    }

    override suspend fun clearHistory() {
        historyRepository.clearHistory()
    }

    override suspend fun historyTracks(): Flow<List<Track>> {
        return historyRepository.historyTracks()
    }

}
package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.HistoryInteractor
import com.practicum.playlistmaker.search.domain.db.HistoryRepository
import kotlinx.coroutines.flow.Flow

class HistoryInteractorImpl(
    private val historyRepository: HistoryRepository,
) : HistoryInteractor {

    override suspend fun saveTrackToHistory(track: Track) {
        historyRepository.saveTrackToHistory(track)
    }

    override suspend fun historyTracks(): Flow<List<Track>> {
        return historyRepository.historyTracks()
    }

    override suspend fun clearHistory() {
        historyRepository.clearHistory()
    }

}
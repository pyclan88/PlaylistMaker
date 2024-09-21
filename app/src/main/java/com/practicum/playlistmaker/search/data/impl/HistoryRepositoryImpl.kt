package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.converter.TrackDbConverter
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.db.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val MAX_NUM_OF_HIST_TRACKS = 10

class HistoryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : HistoryRepository {

    override suspend fun saveTrackToHistory(track: Track) {
        val historyTrackEntity = trackDbConverter.map(track)
        val updatedHistoryTrackEntity = historyTrackEntity.copy(addedAt = System.currentTimeMillis())
        val trackCount = appDatabase.historyTrackDao().getTrackCount()
        if (trackCount == MAX_NUM_OF_HIST_TRACKS) {
            appDatabase.historyTrackDao().deleteOldestTrack()
        }
        appDatabase.historyTrackDao().insertTrack(updatedHistoryTrackEntity)
    }

    override suspend fun historyTracks(): Flow<List<Track>> = flow {
        val historyTrackEntities = appDatabase.historyTrackDao().getAllTracks()
        val tracks = historyTrackEntities.map { track -> trackDbConverter.map(track) }
        emit(tracks)
    }

    override suspend fun clearHistory() {
        appDatabase.historyTrackDao().clearTable()
    }

}
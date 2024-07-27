package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.TrackDbRepository
import com.practicum.playlistmaker.search.domain.TrackDbInteractor

class TrackDbInteractorImpl(private val trackDBRepository: TrackDbRepository) : TrackDbInteractor {
    override fun saveTrack(track: Track) {
        trackDBRepository.saveTrack(track)
    }

    override fun loadTrack(): Track {
        return trackDBRepository.loadTrack()
    }

    override fun loadHistory(): List<Track> {
        return trackDBRepository.loadHistory()
    }

    override fun clearHistory() {
        trackDBRepository.clearHistory()
    }

}
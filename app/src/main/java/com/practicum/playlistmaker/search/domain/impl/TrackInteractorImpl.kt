package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.TrackInteractor

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {
    override fun saveTrack(track: Track) {
        trackRepository.saveTrack(track)
    }

    override fun loadTrack(): Track {
        return trackRepository.loadTrack()
    }

    override fun loadHistory(): List<Track> {
        return trackRepository.loadHistory()
    }

    override fun clearHistory() {
        trackRepository.clearHistory()
    }

}
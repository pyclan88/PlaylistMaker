package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun loadTrack(): Track {
        return repository.loadTrack()
    }

    override fun loadHistory(): List<Track> {
        return repository.loadHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

}
package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.model.Track

interface TrackInteractor {
    fun saveTrack(track: Track)
    fun loadTrack(): Track
    fun loadHistory(): List<Track>
    fun clearHistory()
}
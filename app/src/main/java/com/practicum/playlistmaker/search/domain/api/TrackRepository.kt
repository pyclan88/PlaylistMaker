package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.player.domain.model.Track

interface TrackRepository {
    fun saveTrack(track: Track)
    fun loadTrack(): Track
    fun loadHistory(): List<Track>
    fun clearHistory()
}
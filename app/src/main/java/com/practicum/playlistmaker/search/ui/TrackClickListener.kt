package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.player.domain.model.Track

interface TrackClickListener {
    fun onTrackClick(track: Track)
}
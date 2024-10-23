package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.player.domain.model.Track

fun interface TrackClickListener {
    fun onTrackClick(track: Track)
}
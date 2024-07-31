package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.model.Track

interface SearchInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

}
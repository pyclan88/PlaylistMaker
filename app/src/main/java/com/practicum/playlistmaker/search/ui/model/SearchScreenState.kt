package com.practicum.playlistmaker.search.ui.model

import com.practicum.playlistmaker.player.domain.model.Track

sealed interface SearchScreenState {

    object Loading : SearchScreenState

    data class Content(
        val tracks: List<Track>
    ) : SearchScreenState

    data class Error(
        val message: String
    ) : SearchScreenState

    object Empty : SearchScreenState

}
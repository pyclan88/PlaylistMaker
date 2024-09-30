package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.player.domain.model.Track

sealed interface SearchScreenState {

    object Loading : SearchScreenState

    object EmptySearch : SearchScreenState

    data class SearchContent(
        val tracks: List<Track>
    ) : SearchScreenState

    data class Error(
        val message: String
    ) : SearchScreenState

    object EmptyHistory : SearchScreenState

    data class HistoryContent(
        val tracks: List<Track>
    ) : SearchScreenState

}
package com.practicum.playlistmaker.medialibrary.presentation.favoritetracks

import com.practicum.playlistmaker.player.domain.model.Track

sealed interface FavoriteScreenState {

    data class Content(
        val tracks: List<Track>
    ) : FavoriteScreenState

    object Empty : FavoriteScreenState

}
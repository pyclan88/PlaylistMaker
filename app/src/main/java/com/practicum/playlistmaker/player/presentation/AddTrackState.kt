package com.practicum.playlistmaker.player.presentation

sealed interface AddTrackState {

    data class TrackAdded(
        val playlistName: String
    ): AddTrackState

    data class TrackAlreadyExists(
        val playlistName: String
    ): AddTrackState

}
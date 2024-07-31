package com.practicum.playlistmaker.player.data.mapper

import com.practicum.playlistmaker.player.data.dto.TrackDto
import com.practicum.playlistmaker.player.domain.model.Track

object TrackDtoToTrackMapper {

    fun map(track: TrackDto): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

}
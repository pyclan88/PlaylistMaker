package com.practicum.playlistmaker.search.data.converter

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.db.entities.PlaylistEntity
import com.practicum.playlistmaker.search.data.db.entities.PlaylistTrackEntity

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            idList = playlist.idList,
            count = playlist.count,
            lastModifiedAt = playlist.lastModifiedAt,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            idList = playlist.idList,
            count = playlist.count,
            lastModifiedAt = playlist.lastModifiedAt,
        )
    }

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite,
        )
    }

}
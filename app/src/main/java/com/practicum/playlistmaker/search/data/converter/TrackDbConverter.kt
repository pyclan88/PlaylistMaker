package com.practicum.playlistmaker.search.data.converter

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker.search.data.db.HistoryTrackEntity
import kotlin.reflect.KClass

class TrackDbConverter {

    fun<T : Any> map(track: Track, type: KClass<T>): T {
        return when (type) {
            HistoryTrackEntity::class -> {
                HistoryTrackEntity(
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTimeMillis = track.trackTimeMillis,
                    artworkUrl100 = track.artworkUrl100,
                    trackId = track.trackId,
                    collectionName = track.collectionName,
                    releaseDate = track.releaseDate,
                    primaryGenreName = track.primaryGenreName,
                    country = track.country,
                    previewUrl = track.previewUrl,
                    isFavorite = track.isFavorite,
                ) as T
            }
            FavoriteTrackEntity::class -> {
                FavoriteTrackEntity(
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTimeMillis = track.trackTimeMillis,
                    artworkUrl100 = track.artworkUrl100,
                    trackId = track.trackId,
                    collectionName = track.collectionName,
                    releaseDate = track.releaseDate,
                    primaryGenreName = track.primaryGenreName,
                    country = track.country,
                    previewUrl = track.previewUrl,
                    isFavorite = track.isFavorite,
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown class type")
        }


    }

    fun map(track: FavoriteTrackEntity): Track {
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
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite,
        )
    }

    fun map(track: HistoryTrackEntity): Track {
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
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite,
        )
    }

}
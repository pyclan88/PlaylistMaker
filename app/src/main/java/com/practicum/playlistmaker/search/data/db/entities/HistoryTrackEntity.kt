package com.practicum.playlistmaker.search.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_track_table")
data class HistoryTrackEntity(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    @PrimaryKey
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    val isFavorite: Boolean,
    val addedAt: Long = 0,
)

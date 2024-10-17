package com.practicum.playlistmaker.search.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val idList: String,
    var count: Int,
    val lastModifiedAt: Long,
)

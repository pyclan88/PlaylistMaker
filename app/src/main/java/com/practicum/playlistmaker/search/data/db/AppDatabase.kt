package com.practicum.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.search.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.search.data.db.dao.HistoryTrackDao
import com.practicum.playlistmaker.search.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.search.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.search.data.db.entities.FavoriteTrackEntity
import com.practicum.playlistmaker.search.data.db.entities.HistoryTrackEntity
import com.practicum.playlistmaker.search.data.db.entities.PlaylistEntity
import com.practicum.playlistmaker.search.data.db.entities.PlaylistTrackEntity

@Database(
    version = 2, entities = [
        HistoryTrackEntity::class,
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyTrackDao(): HistoryTrackDao
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}
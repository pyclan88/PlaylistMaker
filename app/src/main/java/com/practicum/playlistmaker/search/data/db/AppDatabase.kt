package com.practicum.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.search.data.db.dao.HistoryTrackDao

@Database(version = 1, entities = [HistoryTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyTrackDao(): HistoryTrackDao
}
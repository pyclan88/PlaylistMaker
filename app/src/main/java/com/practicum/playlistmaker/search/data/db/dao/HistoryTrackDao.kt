package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.data.db.HistoryTrackEntity

@Dao
interface HistoryTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: HistoryTrackEntity)

    @Query("DELETE FROM history_track_table WHERE addedAt = (SELECT MIN(addedAt) FROM history_track_table)")
    suspend fun deleteOldestTrack()

    @Query("SELECT * FROM history_track_table ORDER BY addedAt DESC")
    suspend fun getAllTracks(): List<HistoryTrackEntity>

    @Query("SELECT COUNT(*) FROM history_track_table")
    suspend fun getTrackCount(): Int

    @Query("DELETE FROM history_track_table")
    suspend fun clearTable()

}
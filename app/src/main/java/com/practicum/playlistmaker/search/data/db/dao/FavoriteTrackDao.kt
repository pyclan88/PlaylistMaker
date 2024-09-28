package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.data.db.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete(entity = FavoriteTrackEntity::class)
    suspend fun deleteTrack(favoriteTrackEntity: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_track_table ORDER BY addedAt DESC")
    fun getAllTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getAllIds(): List<Int>

}
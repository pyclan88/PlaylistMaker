package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.search.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY lastModifiedAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT name FROM playlist_table")
    suspend fun getAllNames(): List<String>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    fun getPlaylistById(id: Long): Flow<PlaylistEntity>

    @Query("SELECT COUNT(*) FROM playlist_table WHERE idList LIKE '%' || :trackId || '%'")
    suspend fun countPlaylistsContainingTrack(trackId: Int): Int

    @Query("DELETE FROM playlist_table WHERE id = :id")
    suspend fun deletePlaylist(id: Long)

}
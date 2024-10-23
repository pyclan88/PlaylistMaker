package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.db.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, idList: String) {
        playlistRepository.addTrackToPlaylist(playlist, idList)
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, idList: String, trackId: Int) {
        playlistRepository.updatePlaylistAfterTrackRemoval(playlist, idList, trackId)
    }

    override suspend fun playlists(): Flow<List<Playlist>> {
        return playlistRepository.playlists()
    }

    override suspend fun playlistNames(): Flow<List<String>> {
        return playlistRepository.playlistNames()
    }

    override suspend fun createPlaylist(coverPath: String?, name: String, description: String?) {
        val playlist = Playlist(
            coverPath = coverPath,
            name = name,
            description = description
        )
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Long): Flow<Playlist> {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun addTrackToPlaylistTrack(track: Track) {
        playlistRepository.addTrackToPlaylistTrack(track)
    }

    override suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksByIds(ids)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

}
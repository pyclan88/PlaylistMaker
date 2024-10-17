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

    override suspend fun updatePlaylist(playlist: Playlist, idList: String) {
        playlistRepository.updatePlaylist(playlist, idList)
    }

    override suspend fun playlists(): Flow<List<Playlist>> {
        return playlistRepository.playlists()
    }

    override suspend fun playlistNames(): Flow<List<String>> {
        return playlistRepository.playlistNames()
    }

    override suspend fun addTrackToPlaylistTrack(track: Track) {
        playlistRepository.addTrackToPlaylistTrack(track)
    }

    override suspend fun createPlaylist(coverPath: String?, name: String, description: String?) {
        val playlist = Playlist(
            coverPath = coverPath,
            name = name,
            description = description
        )
        playlistRepository.addPlaylist(playlist)
    }

}
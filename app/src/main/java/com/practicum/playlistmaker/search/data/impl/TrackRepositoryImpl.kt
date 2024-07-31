package com.practicum.playlistmaker.search.data.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.dto.TrackDto
import com.practicum.playlistmaker.player.data.mapper.TrackDtoToTrackMapper
import com.practicum.playlistmaker.player.data.mapper.TrackToTrackDtoMapper
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.TrackRepository


const val SAVED_TRACK_KEY = "key_for_saved_track"
const val HISTORY_TRACKS_KEY = "key_for_history_tracks"

const val MAX_NUM_OF_HIST_TRACKS = 10

class TrackRepositoryImpl(
    private val gson: Gson,
    private val historyTracksSharedPreferences: SharedPreferences,
    private val savedTracksPreferences: SharedPreferences,
) : TrackRepository {

    private fun addTrackToHistory(list: List<TrackDto>) {
        val jsonTracks = gson.toJson(list)
        historyTracksSharedPreferences.edit()
            .putString(HISTORY_TRACKS_KEY, jsonTracks)
            .apply()
    }

    private fun refreshHistory(trackDto: TrackDto) {
        val historyTracks = loadHistory().map { TrackToTrackDtoMapper.map(it) }.toMutableList()
        historyTracks.removeIf { it.trackId == trackDto.trackId }
        if (historyTracks.size == MAX_NUM_OF_HIST_TRACKS) {
            historyTracks.removeAt(historyTracks.size - 1)
        }
        historyTracks.add(0, trackDto)
        addTrackToHistory(historyTracks)
    }

    override fun saveTrack(track: Track) {
        val trackDto = TrackToTrackDtoMapper.map(track)
        val jsonTrack = gson.toJson(trackDto)
        savedTracksPreferences.edit()
            .putString(SAVED_TRACK_KEY, jsonTrack)
            .apply()
        refreshHistory(trackDto)
    }

    override fun loadTrack(): Track {
        val json = savedTracksPreferences.getString(SAVED_TRACK_KEY, null)
        val currentTrack: TrackDto = gson.fromJson(json, TrackDto::class.java)
        return TrackDtoToTrackMapper.map(currentTrack)
    }

    override fun loadHistory(): List<Track> {
        val gsonTracks =
            historyTracksSharedPreferences.getString(HISTORY_TRACKS_KEY, null) ?: return emptyList()
        val historyTracks = gson.fromJson(gsonTracks, Array<Track>::class.java).toList()
        return historyTracks
    }

    override fun clearHistory() {
        val emptyList = emptyList<TrackDto>()
        addTrackToHistory(emptyList)
    }
}
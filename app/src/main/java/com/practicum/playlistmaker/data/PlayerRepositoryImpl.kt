package com.practicum.playlistmaker.data

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.mapper.TrackDtoToTrackMapper
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track


const val SAVED_TRACK_PREFERENCES = "saved_track_preferences"
const val SAVED_TRACK_KEY = "key_for_saved_track"

class PlayerRepositoryImpl(context: Context) : PlayerRepository {

    private var mediaPlayer = MediaPlayer()

    private val sharedPrefs = context.getSharedPreferences(
        SAVED_TRACK_PREFERENCES,
        Context.MODE_PRIVATE
    )
    private val json = sharedPrefs.getString(SAVED_TRACK_KEY, null)
    private val gson = Gson()
    private val currentTrack = gson.fromJson(json, TrackDto::class.java)
    override fun preparePlayer(preparedCallback: () -> Unit, completionCallback: () -> Unit) {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            preparedCallback.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            completionCallback.invoke()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getPlayer(): MediaPlayer {
        return mediaPlayer
    }

    override fun getTrack(): Track {
        return TrackDtoToTrackMapper.map(currentTrack)
    }
}
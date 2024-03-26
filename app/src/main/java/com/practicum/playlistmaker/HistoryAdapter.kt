package com.practicum.playlistmaker

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.data.SAVED_TRACK_KEY
import com.practicum.playlistmaker.data.SAVED_TRACK_PREFERENCES
import com.practicum.playlistmaker.data.mapper.TrackToTrackDtoMapper
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.player.AudioPlayerActivity
import kotlin.collections.ArrayList

class HistoryAdapter(
    private val context: Context,
    private val handler: Handler
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var clickedTracks = ArrayList<Track>()

    private var isClickedAllowed = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = clickedTracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = clickedTracks[position]

        with(holder) {
            bind(track)
            holder.itemView.setOnClickListener {

                if (clickDebounce()) {
                    clickedTracks.removeIf { it.trackId == track.trackId }
                    if (clickedTracks.size == MAX_NUM_OF_HIST_TRACKS) clickedTracks.removeAt(
                        clickedTracks.size - 1
                    )
                    clickedTracks.add(0, track)

                    val gson = Gson()
                    val trackToJson = gson.toJson(TrackToTrackDtoMapper.map(track))
                    val sharedPrefs = context.getSharedPreferences(
                        SAVED_TRACK_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    sharedPrefs.edit()
                        .putString(SAVED_TRACK_KEY, trackToJson)
                        .apply()

                    AudioPlayerActivity.startActivity(holder.itemView.context)
                }

            }
        }

    }

    private fun clickDebounce(): Boolean {
        val current = isClickedAllowed
        if (isClickedAllowed) {
            isClickedAllowed = false
            handler.postDelayed(
                { isClickedAllowed = true },
                SearchActivity.CLICK_DEBOUNCE_DELAY_MILLIS
            )
        }
        return current
    }

}
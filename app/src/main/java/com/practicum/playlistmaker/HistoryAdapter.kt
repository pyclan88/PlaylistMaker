package com.practicum.playlistmaker

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class HistoryAdapter(private val handler: Handler) : RecyclerView.Adapter<TrackViewHolder>() {

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

                    AudioPlayerActivity.startActivity(holder.itemView.context, track)
                }

            }
        }

    }

    private fun clickDebounce(): Boolean {
        val current = isClickedAllowed
        if (isClickedAllowed) {
            isClickedAllowed = false
            handler.postDelayed({ isClickedAllowed = true }, SearchActivity.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}
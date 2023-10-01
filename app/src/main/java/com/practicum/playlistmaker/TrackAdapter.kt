package com.practicum.playlistmaker

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


const val MAX_NUM_OF_HIST_TRACKS = 10

class TrackAdapter(private val historyAdapter: HistoryAdapter,
                   private val handler: Handler) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    private var isClickedAllowed = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        with(holder) {
            bind(track)
            itemView.setOnClickListener {

                if (clickDebounce()) {
                    val historyTracks = historyAdapter.clickedTracks
                    historyTracks.removeIf { it.trackId == track.trackId }
                    if (historyTracks.size == MAX_NUM_OF_HIST_TRACKS) historyTracks.removeAt(
                        historyTracks.size - 1
                    )
                    historyTracks.add(0, track)

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
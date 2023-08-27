package com.practicum.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

const val MAX_NUM_OF_HIST_TRACKS = 10

class TrackAdapter(private val historyAdapter: HistoryAdapter) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.findViewById<LinearLayout>(R.id.track_view).setOnClickListener {
            val historyTracks = historyAdapter.clickedTracks
            historyTracks.removeIf { it.trackId == track.trackId }
            if (historyTracks.size == MAX_NUM_OF_HIST_TRACKS) historyTracks.removeAt(historyTracks.size - 1)
            historyTracks.add(0, track)

            val intent = Intent(holder.itemView.context, AudioPlayerActivity::class.java)
            holder.itemView.context.startActivity(intent)
            AudioPlayerActivity.currentTrack = track
        }
    }
}
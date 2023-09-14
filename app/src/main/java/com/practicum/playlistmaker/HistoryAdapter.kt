package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class HistoryAdapter() : RecyclerView.Adapter<TrackViewHolder>() {

    var clickedTracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = clickedTracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = clickedTracks[position]

        with(holder) {
            bind(track)
            holder.itemView.findViewById<LinearLayout>(R.id.track_view).setOnClickListener {
                AudioPlayerActivity.startActivity(holder.itemView.context, track)
            }
        }

    }
}
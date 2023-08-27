package com.practicum.playlistmaker

import android.content.Intent
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
        holder.bind(track)
        holder.itemView.findViewById<LinearLayout>(R.id.track_view).setOnClickListener {
            val intent = Intent(holder.itemView.context, AudioPlayerActivity::class.java)
            holder.itemView.context.startActivity(intent)
            AudioPlayerActivity.currentTrack = track
        }
    }
}
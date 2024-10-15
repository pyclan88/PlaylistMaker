package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlayerViewBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlayerAdapter(
    private val clickListener: PlaylistClickListener
) : RecyclerView.Adapter<PlayerViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlayerViewHolder(
            PlayerViewBinding.inflate(layoutInspector, parent, false), clickListener
        )
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

}
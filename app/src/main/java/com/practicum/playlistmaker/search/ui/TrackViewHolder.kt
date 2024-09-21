package com.practicum.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.player.domain.model.Track
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.Locale

const val COVER_CORNER_8 = 8

class TrackViewHolder(
    private val binding: TrackViewBinding,
    private val clickListener: TrackClickListener
) : RecyclerView.ViewHolder(binding.root), KoinComponent {

    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(COVER_CORNER_8))
            .into(binding.cover)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis.toLong())

        itemView.setOnClickListener {
            clickListener.onTrackClick(track)
        }
    }

}
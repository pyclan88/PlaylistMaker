package com.practicum.playlistmaker.medialibrary.ui.playlist

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.utils.AppConstants.COVER_CORNER_8
import com.practicum.playlistmaker.utils.WordUtils
import com.practicum.playlistmaker.R

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        if (playlist.coverPath != null) {
            val coverUri = Uri.parse(playlist.coverPath)
            Glide.with(itemView)
                .load(coverUri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(COVER_CORNER_8)
                )
                .into(binding.cover)
        }

        binding.name.text = playlist.name
        val trackCount = playlist.count
        val wordForms = listOf(
            itemView.context.getString(R.string.track_singular),
            itemView.context.getString(R.string.track_few),
            itemView.context.getString(R.string.track_many)
        )
        binding.count.text = WordUtils.getDeclension(trackCount, wordForms)
    }

}
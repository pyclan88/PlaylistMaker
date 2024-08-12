package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.util.DateTimeUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.model.PlayStatus
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerActivity : AppCompatActivity() {

    private lateinit var currentTrack: Track
    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = viewModel.provideCurrentTrack()

        setValues()

        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            renderPlayButton(playStatus)
        }

        viewModel.getProgressLiveData().observe(this) { progress ->
            binding.playbackTime.text =
                DateTimeUtil.formatTime(progress)
        }

        setListeners()

        if (viewModel.isMusicPlaying()) viewModel.play()
    }

    override fun onStop() {
        super.onStop()
        viewModel.forcePause()
    }

    private fun setListeners() {
        binding.backFromAudioPlayer.setOnClickListener {
            finish()
        }
        binding.playButton.setOnClickListener {
            if (viewModel.isPreviewUrlValid()) {
                setPlayButtonAction(viewModel.getPlayStatusLiveData().value)
            } else {
                showEmptySongToast()
            }
        }
    }

    private fun showEmptySongToast() {
        val message = getString(R.string.song_is_not_available)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setPlayButtonAction(playStatus: PlayStatus?) {
        if (playStatus?.isPlaying == true) {
            viewModel.pause()
        } else {
            viewModel.play()
        }
    }

    private fun renderPlayButton(playStatus: PlayStatus?) {
        if (playStatus?.isPlaying == true) {
            binding.playButton.setImageResource(R.drawable.ic_pause_button)
        } else {
            binding.playButton.setImageResource(R.drawable.ic_play_button)
        }
    }

    private fun setValues() {
        binding.trackNamePlayer.text = currentTrack.trackName
        binding.artistNamePlayer.text = currentTrack.artistName
        binding.durationValue.text = DateTimeUtil.formatTime(currentTrack.trackTimeMillis.toInt())
        binding.albumValue.text = currentTrack.collectionName
        binding.yearValue.text = currentTrack.releaseDate.substring(0, 4)
        binding.genreValue.text = currentTrack.primaryGenreName
        binding.countryValue.text = currentTrack.country
        loadCoverToPlayer()
    }

    private fun loadCoverToPlayer() {
        Glide.with(this)
            .load(getCoverArtwork(currentTrack.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner8)))
            .into(binding.cover)
    }

    private fun getCoverArtwork(artworkUrl100: String?): String? {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        }

}
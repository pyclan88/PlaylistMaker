package com.practicum.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.util.DateTimeUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.ui.model.PlayStatus
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var currentTrack: Track

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack = viewModel.provideCurrentTrack()

        setValues()

        viewModel.getPlayStatusLiveData().observe(viewLifecycleOwner) { playStatus ->
            renderPlayButton(playStatus)
        }

        viewModel.getProgressLiveData().observe(viewLifecycleOwner) { progress ->
            binding.playbackTime.text =
                DateTimeUtil.formatTime(progress)
        }

        setListeners()
    }

    override fun onPause() {
        super.onPause()
//        пауза только при сворачивании, при повороте плеер продолжает играть
        if (!requireActivity().isChangingConfigurations) viewModel.pause()
    }

    override fun onStop() {
        super.onStop()
        if (!requireActivity().isChangingConfigurations) viewModel.pause()
    }

    private fun setListeners() {
        binding.backFromAudioPlayer.setOnClickListener {
            findNavController().navigateUp()
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
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
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
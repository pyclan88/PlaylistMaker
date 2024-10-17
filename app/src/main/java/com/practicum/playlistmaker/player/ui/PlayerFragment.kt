package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.utils.DateTimeUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.presentation.AddTrackState
import com.practicum.playlistmaker.player.presentation.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.PlayerState
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.utils.debounce
import com.practicum.playlistmaker.utils.getParcelableCompat
import com.practicum.playlistmaker.utils.invisible
import com.practicum.playlistmaker.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    private var playlistAdapter: PlayerAdapter? = null

    private val playerViewModel by viewModel<PlayerViewModel> {
        parametersOf(requireArguments().getParcelableCompat(ARGS_TRACK))
    }

    private lateinit var currentTrack: Track
    private lateinit var hidePanelDebounce: (Unit) -> Unit

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

        binding.playlistBottomSheet?.let { bottomSheetContainer ->
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay?.invisible()
                    }

                    else -> {
                        binding.overlay?.visible()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        playlistAdapter = PlayerAdapter(
            PlaylistClickListener {
                playerViewModel.addTrackToPlaylist(it)
            }
        )

        binding.rvPlaylist?.adapter = playlistAdapter

        hidePanelDebounce = debounce(
            SHOW_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) {
            binding.landConst?.invisible()
        }

        currentTrack = playerViewModel.provideCurrentTrack()

        setValues()

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
            showPlaylistsInBottomSheet(it)
            updateUI(it)
            setListeners(it)
        }

        playerViewModel.observeAddingTrackState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: AddTrackState) {
        when (state) {
            is AddTrackState.TrackAdded -> showTrackAddedToast(state.playlistName)
            is AddTrackState.TrackAlreadyExists -> showTrackAlreadyExistsToast(state.playlistName)
        }
    }

    private fun showTrackAddedToast(playlistName: String) {
        val messageWhenAdded = getString(R.string.track_added_to_playlist, playlistName)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        Toast.makeText(requireContext(), messageWhenAdded, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showTrackAlreadyExistsToast(playlistName: String) {
        val messageWhenAlreadyHas = getString(R.string.playlist_already_has_track, playlistName)
        Toast.makeText(requireContext(), messageWhenAlreadyHas, Toast.LENGTH_SHORT)
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showPlaylistsInBottomSheet(state: PlayerScreenState) {
        val playlists = state.playlists
        if (!playlists.isNullOrEmpty()) {
            playlistAdapter?.apply {
                this.playlists.clear()
                this.playlists.addAll(playlists)
                this.notifyDataSetChanged()
            }
        }
    }

    private fun updateUI(state: PlayerScreenState) {
        binding.playButton.isEnabled = state.isPlayButtonEnabled
        binding.playButton.setImageResource(
            if (state.playerState == PlayerState.PLAYING) R.drawable.ic_pause_button else R.drawable.ic_play_button
        )
        binding.playbackTime.text = state.progress
        binding.addToFavoriteButton?.setImageDrawable(getFavoriteToggleDrawable(state.isFavorite))
    }

    private fun pauseAfterCollapse() {
        if (!requireActivity().isChangingConfigurations) playerViewModel.onPause()
    }

    private fun setListeners(state: PlayerScreenState) {
        binding.backFromAudioPlayer?.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playButton.setOnClickListener {
            if (playerViewModel.isPreviewUrlNotValid()) {
                showEmptySongToast()
            } else {
                playerViewModel.onPlayClicked()
            }
        }

        binding.addToFavoriteButton?.setOnClickListener {
            playerViewModel.onFavoriteClicked()
        }

        binding.cover.setOnClickListener {
            if (binding.landConst?.isVisible == true) {
                binding.landConst?.invisible()
            } else {
                binding.landConst?.visible()
                hideLandPanel(state)
            }
        }

        binding.addToPlaylistButton?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonNewPlaylist?.setOnClickListener {
            findNavController().navigate(
                R.id.action_playerFragment_to_newPlaylistFragment
            )
        }
    }

    private fun showEmptySongToast() {
        val message = getString(R.string.song_is_not_available)
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    private fun hideLandPanel(state: PlayerScreenState) {
        if (binding.landConst?.isVisible == true && state.playerState == PlayerState.PLAYING) {
            hidePanelDebounce(Unit)
        }
    }

    private fun setValues() {
        binding.trackNamePlayer?.text = currentTrack.trackName
        binding.artistNamePlayer?.text = currentTrack.artistName
        binding.durationValue?.text = DateTimeUtil.formatTime(currentTrack.trackTimeMillis.toInt())
        binding.albumValue?.text = currentTrack.collectionName
        binding.yearValue?.text = currentTrack.releaseDate.substring(0, 4)
        binding.genreValue?.text = currentTrack.primaryGenreName
        binding.countryValue?.text = currentTrack.country
        loadCoverToPlayer()
    }

    private fun getFavoriteToggleDrawable(isFavorite: Boolean): Drawable? {
        return AppCompatResources.getDrawable(
            context ?: return null,
            if (isFavorite) R.drawable.ic_favorite_active else R.drawable.ic_favorite_inactive
        )
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

    override fun onPause() {
        super.onPause()
        pauseAfterCollapse()
    }

    override fun onStop() {
        super.onStop()
        pauseAfterCollapse()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SHOW_DELAY = 4000L
        private const val ARGS_TRACK = "TRACK_KEY"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }

}
package com.practicum.playlistmaker.medialibrary.ui.favoritetracks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.practicum.playlistmaker.medialibrary.presentation.favoritetracks.FavoriteScreenState
import com.practicum.playlistmaker.medialibrary.presentation.favoritetracks.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.TrackClickListener
import com.practicum.playlistmaker.utils.AppConstants
import com.practicum.playlistmaker.utils.debounce
import com.practicum.playlistmaker.utils.invisible
import com.practicum.playlistmaker.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding
        get() = _binding!!

    private var favoriteAdapter: TrackAdapter? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val favoriteTracksViewModel by viewModel<FavoriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            AppConstants.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track = track)
            )
        }

        favoriteAdapter = TrackAdapter(
            object : TrackClickListener {
                override fun onTrackClick(track: Track) {
                    onTrackClickDebounce(track)
                }
            }
        )

        binding.rvFavoriteTrack.adapter = favoriteAdapter

        favoriteTracksViewModel.observerState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteScreenState) {
        when (state) {
            is FavoriteScreenState.Content -> showContent(state.tracks)
            is FavoriteScreenState.Empty -> showEmpty()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        binding.rvFavoriteTrack.visible()
        binding.itsEmpty.invisible()
        favoriteAdapter?.apply {
            this.tracks.clear()
            this.tracks.addAll(tracks)
            this.notifyDataSetChanged()
        }
    }

    private fun showEmpty() {
        binding.itsEmpty.visible()
        binding.rvFavoriteTrack.invisible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

}
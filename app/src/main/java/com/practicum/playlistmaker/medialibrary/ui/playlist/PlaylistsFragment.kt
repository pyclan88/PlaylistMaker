package com.practicum.playlistmaker.medialibrary.ui.playlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.presentation.newplaylist.PlaylistScreenState
import com.practicum.playlistmaker.medialibrary.presentation.playlist.PlaylistViewModel
import com.practicum.playlistmaker.utils.invisible
import com.practicum.playlistmaker.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding
        get() = _binding!!

    private var playlistAdapter: PlaylistAdapter? = null

    private val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_newPlaylistFragment
            )
        }

        playlistAdapter = PlaylistAdapter()
        binding.rvPlaylist.adapter = playlistAdapter

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Content -> showContent(state.playlists)
            is PlaylistScreenState.Empty -> showEmpty()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(playlists: List<Playlist>) {
        binding.rvPlaylist.visible()
        binding.itsEmpty.invisible()
        playlistAdapter?.apply {
            this.playlists.clear()
            this.playlists.addAll(playlists)
            notifyDataSetChanged()
        }
    }

    private fun showEmpty() {
        binding.itsEmpty.visible()
        binding.rvPlaylist.invisible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

}
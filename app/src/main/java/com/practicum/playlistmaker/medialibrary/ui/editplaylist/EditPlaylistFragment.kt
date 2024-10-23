package com.practicum.playlistmaker.medialibrary.ui.editplaylist

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.presentation.newplaylist.NewPlaylistViewModel
import com.practicum.playlistmaker.medialibrary.ui.newplaylist.NewPlaylistFragment
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class EditPlaylistFragment : NewPlaylistFragment() {

    private var playlist: Playlist? = null

    override val newPlaylistViewModel by viewModel<NewPlaylistViewModel> {
        parametersOf(requireArguments().getLong(ARGS_PLAYLIST_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.heading.text = requireContext().getString(R.string.edit_title)
        binding.create.text = requireContext().getString(R.string.save)

        newPlaylistViewModel.observePlaylistState().observe(viewLifecycleOwner) { playlistFromDb ->
            setValues(playlistFromDb)
            playlist = playlistFromDb
        }
    }

    private fun setValues(playlist: Playlist?) {
        playlist?.let { playlist ->
            playlist.coverPath?.let { coverPath ->
                val uriCover = Uri.parse(coverPath)
                binding.pickImage.setImageURI(uriCover)
            }
            binding.nameEditText.setText(playlist.name)
            binding.descriptionEditText.setText(playlist.description)
        }
    }

    override fun setListeners(pickMedia: ActivityResultLauncher<PickVisualMediaRequest?>) {
        binding.pickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.backFromNewPlaylist.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.create.setOnClickListener {
            nameToPlaylist = binding.nameEditText.text.toString()

            binding.descriptionEditText.text.toString().let { description ->
                if (description.isNotBlank()) {
                    descriptionToPlaylist = description
                }
            }

            if (isNameUnique()) {
                showSameNameToast()
            } else {
                newPlaylistViewModel.updatePlaylist(
                    playlist,
                    coverToPlaylist ?: playlist?.coverPath,
                    nameToPlaylist,
                    descriptionToPlaylist
                )
                findNavController().navigateUp()
                showPlaylistSavedToast(nameToPlaylist)
            }
        }
    }

    override fun handleBackNavigation() {
        findNavController().navigateUp()
    }

    private fun isNameUnique(): Boolean {
        val oldName = playlist?.name
        val newName = nameToPlaylist

        return newName != oldName && newPlaylistViewModel.getAllPlaylistNames().contains(newName)
    }

    private fun showPlaylistSavedToast(playlistName: String) {
        val message = requireContext().getString(R.string.playlist_saved_message, playlistName)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "PLAYLIST_ID_KEY"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

}
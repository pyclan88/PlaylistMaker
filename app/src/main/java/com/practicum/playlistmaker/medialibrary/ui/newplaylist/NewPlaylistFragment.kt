package com.practicum.playlistmaker.medialibrary.ui.newplaylist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.presentation.newplaylist.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding
        get() = _binding!!

    private var coverToPlaylist: String? = null
    private lateinit var nameToPlaylist: String
    private var descriptionToPlaylist: String? = null

    private val newPlaylistViewModel by viewModel<NewPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia = registerMediaPicker()

        setListeners(pickMedia)

        setupTextWatcher()

        newPlaylistViewModel.loadNames()

        handleOnBackPressed()
    }

    private fun registerMediaPicker(): ActivityResultLauncher<PickVisualMediaRequest?> {
        return registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.pickImage.setImageURI(uri)
                coverToPlaylist =
                    newPlaylistViewModel.saveImageToPrivateStorage(uri).toString()
            }
        }
    }

    private fun setupTextWatcher() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.create.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })
    }

    private fun showSameNameToast() {
        Toast.makeText(
            requireContext(),
            requireContext().getString(R.string.playlist_name_already_exist),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showPlaylistCreatedSnackBar(view: View, playlistName: String) {
        val message = getString(R.string.playlist_created_message, playlistName)

        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.customColorOnBackground)
        )
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.customBackground))
        }
        snackBar.show()
    }

    private fun showExitConformationDialog() {
        MaterialAlertDialogBuilder(requireActivity(), R.style.CustomAlertDialog)
            .setTitle(getString(R.string.exit_playlist_title))
            .setMessage(getString(R.string.exit_playlist_message))
            .setNeutralButton(getString(R.string.exit_playlist_cancel)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.exit_playlist_confirm)) { dialog, which ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            showExitConformationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val isNameEntered = binding.nameEditText.text?.isNotBlank() == true
        val isDescriptionFilled = binding.descriptionEditText.text?.isNotBlank() == true
        val isImageSelected = coverToPlaylist != null
        return isImageSelected || isNameEntered || isDescriptionFilled
    }

    private fun setListeners(pickMedia: ActivityResultLauncher<PickVisualMediaRequest?>) {
        binding.pickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.backFromNewPlaylist.setOnClickListener {
            handleBackNavigation()
        }

        binding.create.setOnClickListener {
            nameToPlaylist = binding.nameEditText.text.toString()

            binding.descriptionEditText.text.toString().let { description ->
                if (description.isNotBlank()) {
                    descriptionToPlaylist = description
                }
            }

            if (newPlaylistViewModel.getAllPlaylistNames().contains(nameToPlaylist)) {
                showSameNameToast()
            } else {
                newPlaylistViewModel.createPlaylist(
                    coverToPlaylist,
                    nameToPlaylist,
                    descriptionToPlaylist
                )
                findNavController().navigateUp()
                showPlaylistCreatedSnackBar(binding.root, nameToPlaylist)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
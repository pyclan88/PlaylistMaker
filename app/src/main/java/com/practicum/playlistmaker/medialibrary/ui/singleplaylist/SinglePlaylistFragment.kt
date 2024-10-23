package com.practicum.playlistmaker.medialibrary.ui.singleplaylist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSinglePlaylistBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.presentation.singleplaylist.SinglePlaylistViewModel
import com.practicum.playlistmaker.medialibrary.ui.editplaylist.EditPlaylistFragment
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.TrackClickListener
import com.practicum.playlistmaker.utils.DateTimeUtil
import com.practicum.playlistmaker.utils.WordUtils
import com.practicum.playlistmaker.utils.invisible
import com.practicum.playlistmaker.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class SinglePlaylistFragment : Fragment() {

    private var _binding: FragmentSinglePlaylistBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var trackBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var trackAdapter: TrackAdapter? = null

    private val singlePlaylistViewModel by viewModel<SinglePlaylistViewModel> {
        parametersOf(requireArguments().getLong(ARGS_PLAYLIST_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSinglePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setupTrackBottomSheet()

        setupMenuBottomSheet()

        singlePlaylistViewModel.observeCombinedState().observe(viewLifecycleOwner) { state ->
            val playlist: Playlist? = state.first
            val trackList: List<Track>? = state.second
            if (playlist != null && trackList != null) {
                setValues(playlist, trackList)
                setListeners(playlist, trackList)
            }
            trackList?.let {
                showTracks(trackList)
            }
        }
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(
            TrackClickListener { track -> handleTrackClick(track) },
            longClickListener = { track -> handleTrackLongClick(track) }
        )
        binding.rvTracks.adapter = trackAdapter
    }

    private fun handleTrackClick(track: Track) {
        if (isMenuBottomSheetHidden()) {
            navigateToPlayerFragment(track)
        }
    }

    private fun handleTrackLongClick(track: Track) {
        if (isMenuBottomSheetHidden()) {
            showDeleteTrackDialog(track)
        }
    }

    private fun isMenuBottomSheetHidden(): Boolean {
        return menuBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN
    }

    private fun navigateToPlayerFragment(track: Track) {
        findNavController().navigate(
            R.id.action_singlePlaylistFragment_to_playerFragment,
            PlayerFragment.createArgs(track = track)
        )
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.dialog_delete_track_title))
            .setMessage("")
            .setNegativeButton(getString(R.string.dialog_delete_track_no), null)
            .setPositiveButton(getString(R.string.dialog_delete_track_yes)) { dialog, which ->
                singlePlaylistViewModel.deleteTrack(track)
            }
            .show()
    }

    private fun setupMenuBottomSheet() {
        binding.menuBottomSheet.let { bottomSheetContainer ->
            menuBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

            binding.root.post {
                val maxHeight = calculateMenuBottomSheetHeight()
                menuBottomSheetBehavior.peekHeight = maxHeight
                menuBottomSheetBehavior.maxHeight = maxHeight
            }
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.invisible()
                        trackBottomSheetBehavior.isDraggable = true
                    }

                    else -> {
                        binding.overlay.visible()
                        trackBottomSheetBehavior.isDraggable = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
        )
    }

    private fun calculateMenuBottomSheetHeight(): Int {
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val distanceFromBottom = screenHeight / 2
        return distanceFromBottom
    }

    private fun setupTrackBottomSheet() {
        binding.tracksBottomSheet.let { bottomSheetContainer ->
            trackBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

            binding.root.post {
                trackBottomSheetBehavior.peekHeight =
                    calculateTrackBottomSheetHeight(binding.shareButton)
            }
        }
    }

    private fun calculateTrackBottomSheetHeight(view: View): Int {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val distanceFromBottom = screenHeight - (location[1] + view.height)
        return distanceFromBottom
    }

    private fun setListeners(playlist: Playlist, trackList: List<Track>) {
        binding.backFromSinglePlaylist.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.shareButton.setOnClickListener {
            onShareButtonClick(playlist, trackList)
        }

        binding.shareText.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            onShareButtonClick(playlist, trackList)
        }

        binding.editText.setOnClickListener {
            findNavController().navigate(
                R.id.action_singlePlaylistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlistId = playlist.id)
            )
        }

        binding.deletePlaylistText.setOnClickListener {
            showDeletePlaylistDialog(playlist)
        }
    }

    private fun showDeletePlaylistDialog(playlist: Playlist?) {
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.dialog_delete_playlist_title, playlist?.name))
            .setMessage("")
            .setNegativeButton(getString(R.string.dialog_delete_track_no), null)
            .setPositiveButton(getString(R.string.dialog_delete_track_yes)) { dialog, which ->
                findNavController().navigateUp()
                if (playlist != null) {
                    singlePlaylistViewModel.deletePlaylist(playlist)
                }
            }
            .show()
    }

    private fun onShareButtonClick(playlist: Playlist, trackList: List<Track>) {
        val isThereNoTrack = trackList.isEmpty()
        if (isThereNoTrack) {
            showNoTrackToShareToast()
        } else {
            sharePlaylist(playlist, trackList)
        }
    }

    private fun sharePlaylist(playlist: Playlist, trackList: List<Track>) {
        val playlistText = generatePlaylistText(playlist, trackList)
        val sentIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, playlistText)
        }
        val shareIntent = Intent.createChooser(sentIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(shareIntent)
    }

    private fun generatePlaylistText(playlist: Playlist, trackList: List<Track>): String {
        val builder = StringBuilder()
        val playlist = playlist
        val tracks = trackList

        builder.append(playlist.name).append("\n")

        val isThereNoDescription = playlist.description.isNullOrEmpty()
        if (!isThereNoDescription) {
            builder.append(playlist.description).append("\n")
        }

        val trackCount = playlist.count
        val trackCountFormatted = String.format(Locale.ROOT, "%02d", trackCount)
        val tracksString = getString(R.string.track_many)
        builder.append(trackCountFormatted).append(" ${tracksString}\n")

        tracks.forEachIndexed { index, track ->
            builder.append(
                "${index + 1}. ${track.artistName} - ${track.trackName} (${
                    DateTimeUtil.formatTime(
                        track.trackTimeMillis.toInt()
                    )
                })"
            ).append("\n")
        }

        return builder.toString().trimIndent()
    }

    private fun showNoTrackToShareToast() {
        val message = getString(R.string.no_track_to_share)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showTracks(trackList: List<Track>) {
        trackAdapter?.apply {
            tracks.clear()
            tracks.addAll(trackList.sortedByDescending { it.addedAt })
            notifyDataSetChanged()
        }
    }


    private fun setValues(playlist: Playlist, trackList: List<Track>) {
        if (trackList.isEmpty()) {
            binding.nothingImage.visible()
            binding.textNothingFound.visible()
        }

        playlist.coverPath?.let { coverPath ->
            val uriCover = Uri.parse(playlist.coverPath)
            binding.cover.setImageURI(uriCover)
            binding.playerView.cover.setImageURI(uriCover)
        }
        binding.name.text = playlist.name
        binding.description.apply {
            playlist.description?.let {
                text = it
                visible()
            } ?: invisible()
        }
        binding.time.text = formatTime(trackList)
        binding.trackCount.text = formatTrackCount(trackList.size.toInt())

        binding.playerView.name.text = playlist.name
        binding.playerView.count.text = formatTrackCount(trackList.size.toInt())
    }

    private fun formatTime(trackList: List<Track>?): String {
        val durationSum: Int? = trackList?.sumOf { it.trackTimeMillis.toInt() } ?: 0
        val formatedDurationSum = SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)
        val wordForms = listOf(
            requireContext().getString(R.string.minute_singular),
            requireActivity().getString(R.string.minute_few),
            requireContext().getString(R.string.minute_many)
        )
        return WordUtils.getDeclension(formatedDurationSum.toInt(), wordForms)
    }

    private fun formatTrackCount(toInt: Int): String {
        val wordForms = listOf(
            requireContext().getString(R.string.track_singular),
            requireActivity().getString(R.string.track_few),
            requireContext().getString(R.string.track_many)
        )
        return WordUtils.getDeclension(toInt, wordForms)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "PLAYLIST_ID_KEY"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

}
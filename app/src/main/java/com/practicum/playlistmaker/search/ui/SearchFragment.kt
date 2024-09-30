package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.presentation.SearchScreenState
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.utils.AppConstants
import com.practicum.playlistmaker.utils.debounce
import com.practicum.playlistmaker.utils.invisible
import com.practicum.playlistmaker.utils.visible
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private var trackAdapter: TrackAdapter? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var simpleTextWatcher: TextWatcher

    private var latestSearchText: String = ""

    private val searchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            AppConstants.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewLifecycleOwner.lifecycleScope.launch {
                searchViewModel.saveTrackToHistory(track)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track = track)
            )
        }

        trackAdapter = TrackAdapter(
            object : TrackClickListener {
                override fun onTrackClick(track: Track) {
                    onTrackClickDebounce(track)
                }
            }
        )

        binding.rvTracks.adapter = trackAdapter

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()

                if (binding.searchEditText.hasFocus() && currentText.isEmpty()) {
                    searchViewModel.showHistory()
                } else {
                    if (currentText != latestSearchText) {
                        searchViewModel.searchDebounce(changedText = s.toString(), false)
                    }
                    latestSearchText = currentText
                }
                binding.clearIcon.visibility =
                    if (currentText.isEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        simpleTextWatcher.let { binding.searchEditText.addTextChangedListener(simpleTextWatcher) }

        setListeners()

        searchViewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleTextWatcher.let { binding.searchEditText.addTextChangedListener(simpleTextWatcher) }
        _binding = null
    }

    private fun setListeners() {
        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText(R.string.empty_string)
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                searchViewModel.showHistory()
            }
        }

        binding.clearHistory.setOnClickListener {
            binding.clearHistory.invisible()
            viewLifecycleOwner.lifecycleScope.launch {
                searchViewModel.clearHistory()
            }
        }

        binding.refreshButton.setOnClickListener {
            searchViewModel.searchDebounce(latestSearchText, true)
        }
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.SearchContent -> showSearch(state.tracks)
            is SearchScreenState.Error -> showError(state.message)
            is SearchScreenState.EmptySearch -> showEmptySearch()
            is SearchScreenState.HistoryContent -> showHistory(state.tracks)
            is SearchScreenState.EmptyHistory -> showEmptyHistory()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showEmptyHistory() {
        binding.clearHistory.invisible()
        binding.youSearched.invisible()
        trackAdapter?.tracks?.clear()
        trackAdapter?.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.progressBar.visible()
        binding.youSearched.invisible()
        binding.clearHistory.invisible()
        binding.searchScroll.invisible()
        binding.linearNothingFound.invisible()
        binding.linearInternetError.invisible()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(tracks: List<Track>) {
        binding.youSearched.visible()
        binding.clearHistory.visible()
        binding.searchScroll.visible()
        binding.rvTracks.visible()
        trackAdapter?.apply {
            this.tracks.clear()
            this.tracks.addAll(tracks)
            this.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearch(tracks: List<Track>) {
        binding.progressBar.invisible()
        binding.youSearched.invisible()
        binding.searchScroll.visible()
        binding.rvTracks.visible()
        trackAdapter?.apply {
            this.tracks.clear()
            this.tracks.addAll(tracks)
            this.notifyDataSetChanged()
        }
    }

    private fun showError(message: String) {
        binding.textInternetError.text = message
        binding.progressBar.invisible()
        binding.searchScroll.invisible()
        binding.linearInternetError.visible()
    }

    private fun showEmptySearch() {
        binding.progressBar.invisible()
        binding.searchScroll.invisible()
        binding.linearNothingFound.visible()
    }

}
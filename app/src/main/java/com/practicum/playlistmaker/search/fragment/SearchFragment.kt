package com.practicum.playlistmaker.search.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.TrackClickListener
import com.practicum.playlistmaker.search.ui.model.SearchScreenState
import com.practicum.playlistmaker.search.ui.viewmodel.HistoryAdapter
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.util.invisible
import com.practicum.playlistmaker.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private val searchAdapter = TrackAdapter(
        object : TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
                }
            }
        }
    )

    private val historyAdapter = HistoryAdapter(
        object : TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
                }
            }

        }
    )

    private lateinit var simpleTextWatcher: TextWatcher

    private var isClickedAllowed = true
    private var latestSearchText: String = ""
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<SearchViewModel>()

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

        binding.rvSearchTrack.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearchTrack.adapter = searchAdapter

        binding.rvHistoryTrack.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvHistoryTrack.adapter = historyAdapter

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText(R.string.empty_string)
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) showHistory() else hideHistory()
            Log.d(TAG, binding.rvSearchTrack.isVisible.toString())
            Log.d(TAG, binding.rvHistoryTrack.isVisible.toString())
        }

        binding.clearHistory.setOnClickListener {
            binding.youSearched.invisible()
            binding.clearHistory.invisible()
            viewModel.clearHistory()
            searchAdapter.tracks.clear()
            searchAdapter.notifyDataSetChanged()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounce(latestSearchText, true)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) showHistory() else hideHistory()
                binding.clearIcon.visibility = clearButtonVisibility(s)
                viewModel.searchDebounce(changedText = s?.toString() ?: "", false)
                latestSearchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        simpleTextWatcher.let { binding.searchEditText.addTextChangedListener(simpleTextWatcher) }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.Content -> showContent(state.tracks)
            is SearchScreenState.Error -> showError(state.message)
            is SearchScreenState.Empty -> showEmpty()
        }
    }

    private fun showHistory() {
        if (viewModel.loadHistory().isNotEmpty()) {
            historyAdapter.tracks = viewModel.loadHistory()
            binding.rvSearchTrack.invisible()
            binding.clearHistory.visible()
            binding.rvHistoryTrack.visible()
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun hideHistory() {
        binding.clearHistory.invisible()
        binding.rvHistoryTrack.invisible()
    }

    private fun showLoading() {
        binding.progressBar.visible()
        binding.youSearched.invisible()
        binding.clearHistory.invisible()
        binding.searchScroll.invisible()
        binding.linearNothingFound.invisible()
        binding.linearInternetError.invisible()
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.invisible()
        binding.searchScroll.visible()
        binding.rvSearchTrack.visible()
        searchAdapter.tracks.clear()
        searchAdapter.tracks.addAll(tracks)
        searchAdapter.notifyDataSetChanged()
    }

    private fun showError(message: String) {
        binding.textInternetError.text = message
        binding.progressBar.invisible()
        binding.searchScroll.invisible()
        binding.linearInternetError.visible()
    }

    private fun showEmpty() {
        binding.progressBar.invisible()
        binding.searchScroll.invisible()
        binding.linearNothingFound.visible()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickedAllowed
        if (isClickedAllowed) {
            isClickedAllowed = false
            mainThreadHandler.postDelayed({ isClickedAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val TAG = "TAG"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }

}
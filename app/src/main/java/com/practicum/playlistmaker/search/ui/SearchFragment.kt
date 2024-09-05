package com.practicum.playlistmaker.search.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.presentation.SearchScreenState
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.util.debounce
import com.practicum.playlistmaker.util.invisible
import com.practicum.playlistmaker.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private var searchAdapter: TrackAdapter? = null
    private var historyAdapter: HistoryAdapter? = null

    private lateinit var onTrackClickDebounce: (Unit) -> Unit

    private lateinit var simpleTextWatcher: TextWatcher

    private var latestSearchText: String = ""

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

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment
            )
        }

        searchAdapter = TrackAdapter(
            object : TrackClickListener {
                override fun onTrackClick() {
                    onTrackClickDebounce(Unit)
                }

            }
        )

        historyAdapter = HistoryAdapter(
            object : TrackClickListener {
                override fun onTrackClick() {
                    onTrackClickDebounce(Unit)
                }
            }
        )

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
        }

        binding.clearHistory.setOnClickListener {
            binding.youSearched.invisible()
            binding.clearHistory.invisible()
            viewModel.clearHistory()
            searchAdapter?.tracks?.clear()
            searchAdapter?.notifyDataSetChanged()
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
        if (viewModel.getHistory().isNotEmpty()) {
            historyAdapter?.tracks = viewModel.getHistory()
            binding.rvSearchTrack.invisible()
            binding.clearHistory.visible()
            binding.rvHistoryTrack.visible()
            historyAdapter?.notifyDataSetChanged()
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
        searchAdapter?.tracks?.clear()
        searchAdapter?.tracks?.addAll(tracks)
        searchAdapter?.notifyDataSetChanged()
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

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }

}
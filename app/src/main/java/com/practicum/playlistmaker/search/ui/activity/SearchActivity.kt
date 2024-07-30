package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.model.SearchScreenState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.util.invisible
import com.practicum.playlistmaker.util.visible

class SearchActivity : AppCompatActivity() {

    private val adapter = TrackAdapter(
        this,
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    )

    private lateinit var simpleTextWatcher: TextWatcher
    private lateinit var binding: ActivitySearchBinding

    private var isClickedAllowed = true
    private var latestSearchText: String = ""
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvSearchTrack.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSearchTrack.adapter = adapter

        showHistory()

        binding.backFromSearchButton.setOnClickListener {
            MainActivity.startActivity(this)
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText(R.string.empty_string)
            showHistory()
        }

        binding.clearHistory.setOnClickListener {
            binding.youSearched.invisible()
            binding.clearHistory.invisible()
            viewModel.clearHistory()
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounce(latestSearchText, true)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                viewModel.searchDebounce(changedText = s?.toString() ?: "", false)
                latestSearchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) showHistory()
            }
        }

        simpleTextWatcher.let { binding.searchEditText.addTextChangedListener(simpleTextWatcher) }

        viewModel.observeState().observe(this) {
            render(it)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher.let { binding.searchEditText.addTextChangedListener(simpleTextWatcher) }
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
            adapter.tracks = viewModel.loadHistory()
            binding.clearHistory.visible()
            adapter.notifyDataSetChanged()
        }
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
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
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
        const val CLICK_DEBOUNCE_DELAY = 1_000L

        fun startActivity(context: Context) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }
    }
}
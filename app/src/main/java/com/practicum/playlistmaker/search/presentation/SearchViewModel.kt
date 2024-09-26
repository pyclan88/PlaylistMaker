package com.practicum.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.db.HistoryInteractor
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor,
) : ViewModel() {

    init {
        loadHistory()
    }

    private val screenStateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = screenStateLiveData

    private val historyListLiveData = MutableLiveData<List<Track>>()
    fun observeHistoryList(): LiveData<List<Track>> = historyListLiveData

    private var latestSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY_MILLIS,
        viewModelScope,
        true
    ) { changedText ->
        searchRequest(changedText)
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyInteractor.clearHistory()
            loadHistory()
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            historyInteractor.addTrackToHistory(track)
            loadHistory()
        }
    }

    fun searchDebounce(changedText: String, refresh: Boolean) {
        if (latestSearchText != changedText && refresh) {
            return
        } else {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            historyInteractor.historyTracks()
                .collect { tracks ->
                    historyListLiveData.postValue(tracks)
                }
        }
    }

    private fun searchRequest(searchInput: String) {
        if (searchInput.isNotEmpty()) {
            setState(SearchScreenState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(searchInput)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = ArrayList<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                setState(SearchScreenState.Error(errorMessage))
            }

            tracks.isEmpty() -> {
                setState(SearchScreenState.Empty)
            }

            else -> {
                setState(SearchScreenState.Content(tracks = tracks))
            }
        }
    }

    private fun setState(state: SearchScreenState) {
        screenStateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2_000L
    }

}
package com.practicum.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.db.HistoryInteractor
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor,
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = screenStateLiveData

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
        }
        setState(SearchScreenState.EmptyHistory)
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            historyInteractor.addTrackToHistory(track)
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

     fun showHistory() {
        viewModelScope.launch {
            historyInteractor.historyTracks()
                .collect { tracks ->
                    processHistory(tracks)
                }
        }
    }

    private fun processHistory(historyTracks: List<Track>?) {
        val tracks = ArrayList<Track>()
        if (historyTracks != null) {
            tracks.addAll(historyTracks)
        }
        if (tracks.isNotEmpty()) {
            setState(SearchScreenState.HistoryContent(tracks))
        } else {
            setState(SearchScreenState.EmptyHistory)
        }
    }

    private fun searchRequest(searchInput: String) {
        if (searchInput.isNotEmpty()) {
            setState(SearchScreenState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(searchInput)
                    .collect { pair ->
                        processSearch(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processSearch(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = ArrayList<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                setState(SearchScreenState.Error(errorMessage))
            }

            tracks.isEmpty() -> {
                setState(SearchScreenState.EmptySearch)
            }

            else -> {
                setState(SearchScreenState.SearchContent(tracks = tracks))
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
package com.practicum.playlistmaker.search.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.TrackInteractor
import com.practicum.playlistmaker.search.ui.model.SearchScreenState

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val trackInteractor: TrackInteractor,
) : ViewModel() {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val screenStateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = screenStateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        mainThreadHandler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun clearHistory() {
        trackInteractor.clearHistory()
    }

    fun loadHistory(): ArrayList<Track> {
        val historyTracks =
            ArrayList(trackInteractor.loadHistory())
        return historyTracks
    }

    fun searchDebounce(changedText: String, refresh: Boolean) {
        if (latestSearchText == changedText && !refresh) {
            return
        }

        this.latestSearchText = changedText

        mainThreadHandler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_MILLIS
        mainThreadHandler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    private fun searchRequest(searchInput: String) {
        if (searchInput.isNotEmpty()) {
            setState(SearchScreenState.Loading)

            searchInteractor.searchTracks(searchInput, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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

            })
        }
    }

    private fun setState(state: SearchScreenState) {
        screenStateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

}
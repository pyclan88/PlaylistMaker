package com.practicum.playlistmaker.search.ui.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.TrackDbInteractor
import com.practicum.playlistmaker.search.ui.model.SearchScreenState

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val trackDbInteractor: TrackDbInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.provideSearchInteractor(context),
                    Creator.provideTrackDbInteractor(context),
                )
            }
        }
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val screenStateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = screenStateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        mainThreadHandler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun clearHistory() {
        trackDbInteractor.clearHistory()
    }

    fun loadHistory(): ArrayList<Track> {
        val historyTracks =
            ArrayList(trackDbInteractor.loadHistory())
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
            renderState(SearchScreenState.Loading)

            searchInteractor.searchTracks(searchInput, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = ArrayList<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }
                    when {
                        errorMessage != null -> {
                            renderState(SearchScreenState.Error(errorMessage))
                        }

                        tracks.isEmpty() -> {
                            renderState(SearchScreenState.Empty)
                        }

                        else -> {
                            renderState(SearchScreenState.Content(tracks = tracks))
                        }
                    }
                }

            })
        }
    }

    private fun renderState(state: SearchScreenState) {
        screenStateLiveData.postValue(state)
    }
}
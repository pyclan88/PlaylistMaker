package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson


class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val historyList = HistoryAdapter.clickedTracks

    fun saveHistory() {
        val jsonHistoryOfTracks = Gson().toJson(historyList)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, jsonHistoryOfTracks)
            .apply()
    }

    fun readHistory(): Array<Track> {
        val jsonHistoryOfTracks =
            sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(jsonHistoryOfTracks, Array<Track>::class.java)
    }
}
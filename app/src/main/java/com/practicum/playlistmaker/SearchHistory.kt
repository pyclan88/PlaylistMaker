package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track


class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun saveHistory(historyList: ArrayList<Track>) {
        val jsonHistoryOfTracks = Gson().toJson(historyList)
        sharedPreferences.edit()
            ?.putString(SearchActivity.SEARCH_HISTORY_KEY, jsonHistoryOfTracks)
            ?.apply()
    }

    fun readHistory(): List<Track> {
        val jsonHistoryOfTracks =
            sharedPreferences.getString(SearchActivity.SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return Gson().fromJson(jsonHistoryOfTracks, Array<Track>::class.java).toList()
    }
}
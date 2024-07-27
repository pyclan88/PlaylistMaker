package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.data.dto.ITunesResponse
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.util.Resource

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(SearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проблемы со связью\nЗагрузка не удалась. Проверьте\n" +
                        "подключение к интернету")
            }

            200 -> {
                Resource.Success((response as ITunesResponse).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}
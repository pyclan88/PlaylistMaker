package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.data.dto.ITunesResponse
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val context: Context,
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SearchRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.Error(context.getString(R.string.search_error_minus1)))
            200 -> {
                val favoriteIds = appDatabase.favoriteTrackDao().getAllIds()
                val data = (response as ITunesResponse).results.map {
                    Track(
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        trackId = it.trackId,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl,
                        isFavorite = favoriteIds.contains(it.trackId)
                    )
                }
                emit(Resource.Success(data))
            }

            else -> emit(Resource.Error(context.getString(R.string.search_error_else)))
        }
    }

}
package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import java.net.URL


class RetrofitNetworkClient(
    private val iTunesService: ITunesApi,
    private val context: Context
) : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    override fun doRequest(dto: Any): Response {
        if (isConnected() == false) {
            return Response().apply { resultCode = -1 }
        }
        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        val response = iTunesService.search(dto.expression).execute()
        val body = response.body()

        return if (body != null) {
            body.apply { resultCode = response.code() }
        } else {
            Response().apply { resultCode = response.code() }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return isServerConnected(
                    iTunesBaseUrl
                )
            }
        }
        return false
    }

    private fun isServerConnected(serverUrl: String): Boolean {
        return try {
            val myUrl = URL(serverUrl)
            val connection = myUrl.openConnection()
            connection.connectTimeout = 10000
            connection.connect()
            true
        } catch (e: Exception) {
            false
        }
    }

}
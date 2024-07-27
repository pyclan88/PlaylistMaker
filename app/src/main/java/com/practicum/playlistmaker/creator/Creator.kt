package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.api.TrackDbRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackDbRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.TrackDbInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TrackDbInteractorImpl
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(context))
    }

    private fun getPlayerRepository(context: Context): PlayerRepository {
        return PlayerRepositoryImpl(context)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(context))
    }

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackDbInteractor(context: Context): TrackDbInteractor {
        return TrackDbInteractorImpl(getTrackDBRepository(context))
    }

    private fun getTrackDBRepository(context: Context): TrackDbRepository {
        return TrackDbRepositoryImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            context,
            getExternalNavigator(context),
        )
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

}
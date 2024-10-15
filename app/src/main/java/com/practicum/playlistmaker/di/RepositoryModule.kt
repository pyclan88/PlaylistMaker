package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.converter.PlaylistDbConverter
import com.practicum.playlistmaker.search.data.converter.TrackDbConverter
import com.practicum.playlistmaker.search.data.impl.FavoriteRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.db.HistoryRepository
import com.practicum.playlistmaker.search.domain.db.PlaylistRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(
            mediaPlayer = get(),
        )
    }

    single<SearchRepository> {
        SearchRepositoryImpl(
            context = get(),
            networkClient = get(),
            appDatabase = get(),
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            sharedPreferences = get(named("themeSwitchPrefs"))
        )
    }

    factory { TrackDbConverter() }

    single<HistoryRepository> {
        HistoryRepositoryImpl(
            appDatabase = get(),
            trackDbConverter = get(),
        )
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(
            appDatabase = get(),
            trackDbConverter = get(),
        )
    }

    factory { PlaylistDbConverter() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            appDatabase = get(),
            playlistDbConverter = get(),
        )
    }

}
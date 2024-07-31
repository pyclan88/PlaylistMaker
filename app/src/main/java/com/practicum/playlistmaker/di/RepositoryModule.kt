package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.api.TrackRepository
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
        )
    }

    single<TrackRepository> {
        TrackRepositoryImpl(
            gson = get(),
            historyTracksSharedPreferences = get(named("historyTracksPrefs")),
            savedTracksPreferences = get(named("savedTracksPrefs")),
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            sharedPreferences = get(named("themeSwitchPrefs"))
        )
    }

}
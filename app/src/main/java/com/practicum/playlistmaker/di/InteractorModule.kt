package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.db.HistoryInteractor
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInteractor> {
        PlayerInteractorImpl(
            repository = get(),
        )
    }

    single<SearchInteractor> {
        SearchInteractorImpl(
            repository = get(),
        )
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            context = get(),
            externalNavigator = get(),
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(
            settingsRepository = get(),
        )
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(
            historyRepository = get(),
        )
    }

}
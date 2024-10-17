package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialibrary.domain.ImageInteractor
import com.practicum.playlistmaker.medialibrary.domain.impl.ImageInteractorImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.search.domain.db.HistoryInteractor
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.impl.FavoriteInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.PlaylistInteractorImpl
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

    factory<SearchInteractor> {
        SearchInteractorImpl(
            repository = get(),
        )
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(
            context = get(),
            externalNavigator = get(),
        )
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(
            settingsRepository = get(),
        )
    }

    factory<HistoryInteractor> {
        HistoryInteractorImpl(
            historyRepository = get(),
        )
    }

    factory<FavoriteInteractor> {
        FavoriteInteractorImpl(
            favoriteRepository = get(),
        )
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(
            playlistRepository = get(),
        )
    }

    factory<ImageInteractor> {
        ImageInteractorImpl(
            imageRepository = get(),
        )
    }

}
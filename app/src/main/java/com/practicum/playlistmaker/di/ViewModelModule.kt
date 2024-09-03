package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialibrary.presentation.FavoriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(
            playerInteractor = get(),
        )
    }

    viewModel {
        SearchViewModel(
            searchInteractor = get(),
            trackInteractor = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get(),
        )
    }

    viewModel {
        FavoriteTracksViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}
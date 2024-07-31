package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
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

}
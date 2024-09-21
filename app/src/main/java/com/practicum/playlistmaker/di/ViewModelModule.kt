package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get(),
        )
    }

    viewModel {
        SearchViewModel(
            searchInteractor = get(),
            historyInteractor = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get(),
        )
    }

}
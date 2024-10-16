package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialibrary.presentation.favoritetracks.FavoriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.presentation.newplaylist.NewPlaylistViewModel
import com.practicum.playlistmaker.medialibrary.presentation.playlist.PlaylistViewModel
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get(),
            favoriteInteractor = get(),
            playlistInteractor = get(),
            gson = get(),
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

    viewModel {
        FavoriteTracksViewModel(
            favoriteInteractor = get(),
        )
    }

    viewModel {
        NewPlaylistViewModel(
            playlistInteractor = get(),
            imageInteractor = get(),
            application = androidApplication()
        )
    }

    viewModel {
        PlaylistViewModel(
            playlistInteractor = get(),
        )
    }

}
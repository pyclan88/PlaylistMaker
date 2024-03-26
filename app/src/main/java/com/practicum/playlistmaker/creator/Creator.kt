package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository

object Creator {

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(context))
    }

    private fun getPlayerRepository(context: Context): PlayerRepository {
        return PlayerRepositoryImpl(context)
    }
}
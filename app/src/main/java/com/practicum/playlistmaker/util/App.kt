package com.practicum.playlistmaker.util

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val condition = Creator.provideSettingsInteractor(this).getThemeSettings().darkTheme
        Creator.provideSettingsInteractor(this).switchTheme(condition)
    }
}
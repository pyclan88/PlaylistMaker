package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val THEME_SWITCH_PREFERENCES = "theme_switch_preferences"
const val THEME_SWITCH_KEY = "key_for_theme_switch"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(THEME_SWITCH_PREFERENCES, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(THEME_SWITCH_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
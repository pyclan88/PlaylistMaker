package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

const val THEME_SWITCH_KEY = "key_for_theme_switch"

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        val themeSettings = ThemeSettings()
        themeSettings.darkTheme = sharedPreferences.getBoolean(THEME_SWITCH_KEY, false)
        return themeSettings
    }

    override fun updateThemeSettings(themeSettings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(THEME_SWITCH_KEY, themeSettings.darkTheme)
            .apply()
    }

    override fun switchTheme(checked: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (checked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        val themeSettings = ThemeSettings()
        themeSettings.darkTheme = checked
        updateThemeSettings(themeSettings)
    }
}
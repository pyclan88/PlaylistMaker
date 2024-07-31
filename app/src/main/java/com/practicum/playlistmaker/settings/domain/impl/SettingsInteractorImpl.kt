package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository,
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun switchTheme(checked: Boolean) {
        settingsRepository.switchTheme(checked)
    }

}
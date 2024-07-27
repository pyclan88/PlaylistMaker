package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(appLink: String)
    fun openLink(termsLink: String)
    fun openEmail(emailData: EmailData)
}
package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        val textToShare = context.getString(R.string.share_link)
        return textToShare
    }

    private fun getTermsLink(): String {
        val websiteUrl = context.getString(R.string.agreement_website)
        return websiteUrl
    }

    private fun getSupportEmailData(): EmailData {
        val recipientEmail = context.getString(R.string.email_recipient)
        val subject = context.getString(R.string.email_subject)
        val message = context.getString(R.string.email_message)
        return EmailData(
            email = recipientEmail,
            subject = subject,
            text = message
        )
    }
}
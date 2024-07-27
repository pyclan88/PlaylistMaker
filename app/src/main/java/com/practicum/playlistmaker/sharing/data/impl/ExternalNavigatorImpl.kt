package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(appLink: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun openLink(termsLink: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
        agreementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(agreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.text)
        }
        val chooserIntent = Intent.createChooser(emailIntent, null)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
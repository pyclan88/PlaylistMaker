package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backFromSettingsButton)
        backButton.setOnClickListener { finish() }

        val shareButton = findViewById<Button>(R.id.share_button)
        shareButton.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                val textToShare = "https://practicum.yandex.ru/profile/android-developer/"
                putExtra(Intent.EXTRA_TEXT, textToShare)
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val supportButton = findViewById<Button>(R.id.support_button)
        supportButton.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                val recipientEmail = "gumabemc@yandex.ru"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
                putExtra(Intent.EXTRA_SUBJECT, subject)
                val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            val supportIntent = Intent.createChooser(sendIntent, null)
            startActivity(supportIntent)
        }

        val agreementButton = findViewById<Button>(R.id.agreement_button)
        agreementButton.setOnClickListener {
            val websiteUrl = "https://yandex.ru/legal/practicum_offer/"
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(agreementIntent)
        }

    }
}